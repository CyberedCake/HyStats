package net.cybercake.hystats.commands.stats;

import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.commands.flags.Arguments;
import net.cybercake.hystats.commands.processors.Processors;
import net.cybercake.hystats.hypixel.CachedPlayer;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.Pair;
import net.cybercake.hystats.utils.TriState;
import net.cybercake.hystats.utils.UChat;
import net.cybercake.hystats.utils.UUIDUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.cybercake.hystats.utils.UChat.format;
import static net.cybercake.hystats.utils.UChat.separator;

public class RequestProcessor {

    public static RequestParameters create() {
        return new RequestParameters();
    }

    protected final RequestParameters params;

    private RequestProcessor(RequestParameters params) {
        this.params = params;
    }

    public Pair<IChatComponent, GameStats> processRequest(String requestedPlayer) {
        try {
            this.params.command.messages.clear();

            long mss = System.currentTimeMillis();
            UUID user = UUIDUtils.processUUID(requestedPlayer);
            CachedPlayer player = HyStats.hypixel.getPlayer(user, UUIDUtils.isUUID(requestedPlayer) ? null : requestedPlayer);

            System.out.println("[" + (System.currentTimeMillis() - mss) + "ms] Loading stats for " + user + "...");
            if (player.isExpired()) {
                if (this.params.showUtilityMessages) {
                    UChat.send(format("&7&oLoading stats, please wait...", null, false));
                }
                player.grab();
            }

            GameStats stats = player.asGameStats(this.params.command.prefix, this.params.args);
            System.out.println("[" + (System.currentTimeMillis() - mss) + "ms] Printing stats for " + user + " (user: " + stats.getUser() + ") in category " + this.params.command.name);
            if (stats.isStaffStatsHidden().bool()) {
                System.out.println("[" + (System.currentTimeMillis() - mss) + "ms] Searched for staff with their information hidden!");
            }

            this.params.command.execute(this.params.sender, stats, this.params.args, this.params.compact);

            IChatComponent sent = this.params.showUtilityMessages ? separator() : UChat.format("");

            int index = 0;
            for (IChatComponent chat : this.params.command.messages) {
                sent = sent.appendSibling(UChat.format(this.params.compact ? (index > 0 ? " &8| " : "") : "\n"))
                        .appendSibling(chat);
                index++;
            }
            sent = sent.appendSibling(this.params.showUtilityMessages ? separator() : UChat.format(""));

            System.out.println("Returning: " + sent.getUnformattedText() + ", stats=" + stats);

            return new Pair<>(sent, stats);
        } catch (Exception error) {
            System.out.println("Returning: error for " + error);
            return new Pair<>(this.params.manager.getError(error, this.params.showUtilityMessages), null);
        }
    }

    public MassSearchPlayersUtility mass() {
        return new MassSearchPlayersUtility(this);
    }

    public static class RequestParameters {

        private RequestParameters() { }

        StatsCommandManager manager;
        ICommandSender sender;
        StatsCategoryCommand command;
        Arguments args;
        Processors processors;
        boolean compact, showUtilityMessages;

        public RequestParameters manager(StatsCommandManager manager) {
            this.manager = manager; return this;
        }

        public RequestParameters sender(ICommandSender sender) {
            this.sender = sender; return this;
        }

        public RequestParameters command(StatsCategoryCommand command) {
            this.command = command; return this;
        }

        public RequestParameters args(Arguments args) {
            this.args = args; return this;
        }

        public RequestParameters processors(Processors processors) {
            this.processors = processors; return this;
        }

        public RequestParameters compact(boolean compact) {
            this.compact = compact; return this;
        }

        public RequestParameters showUtilityMessages(boolean showUtilityMessages) {
            this.showUtilityMessages = showUtilityMessages; return this;
        }

        public RequestProcessor build() {
            return new RequestProcessor(this);
        }

    }

}
