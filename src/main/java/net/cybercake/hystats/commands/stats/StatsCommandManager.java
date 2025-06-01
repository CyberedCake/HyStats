package net.cybercake.hystats.commands.stats;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.commands.flags.Arguments;
import net.cybercake.hystats.commands.stats.categories.*;
import net.cybercake.hystats.events.CheckPartyList;
import net.cybercake.hystats.exceptions.HyStatsError;
import net.cybercake.hystats.utils.UChat;
import net.cybercake.hystats.utils.UTabCompletions;
import net.cybercake.hystats.utils.UUIDUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static net.cybercake.hystats.utils.UChat.*;

public class StatsCommandManager extends CommandBase {

    private static final List<StatsCategoryCommand> commands = new ArrayList<>();

    public StatsCommandManager() {
        commands.add(new BasicStats());
        commands.add(new BedWars());
        commands.add(new MurderMystery());
        commands.add(new SkyWars());
        commands.add(new Socials());
    }

    @Override
    public String getCommandName() {
        return "stats";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/stats <player> [<game>]";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>(Collections.singletonList("hystats"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        try {
            if (!HyStats.hypixel.isApiEnabled()) {
                send(format("&cThe Hypixel API has been disabled and cannot continue.\n&7&oCheck logs for more information! Try restarting your game.", null, true));
                return;
            }

            if (args.length < 1) {
                send(format("&cInvalid usage! &7" + this.getCommandUsage(sender), null, true)); return;
            }

            boolean compact;
            if (args[0].startsWith(":")) {
                compact = true;
                args[0] = args[0].substring(1);
            } else {
                compact = false;
            }

            String requestedPlayer = args[0].replace(".", sender.getName());

            StatsCategoryCommand command = null;
            Arguments arguments = new Arguments(Arrays.copyOfRange(args, Math.min(2, args.length), args.length));
            if (args.length == 1) {
                command = new BasicStats();
            }
            System.out.println("Using arguments: " + arguments.toString());

            for (StatsCategoryCommand cmd : commands) {
                if (command != null) {
                    break;
                }
                if (args[1].equalsIgnoreCase(cmd.name)
                        || (cmd.aliases.length > 0 && Arrays.stream(cmd.aliases).anyMatch(s -> s.equalsIgnoreCase(args[1])))
                ) {
                    command = cmd;
                }
            }

            if (command == null) {
                send(format("&cInvalid category: &8" + args[1] + "\n&cType &7/stats help &cfor help!", null, true));
                return;
            }

            RequestProcessor processor = RequestProcessor.create()
                    .manager(this)
                    .command(command)
                    .sender(sender)
                    .args(arguments)
                    .compact(compact)
                    .showUtilityMessages(true)
                    .build();

            if (ImmutableList.of("-a", "-all", "--a", "--all", "*").contains(requestedPlayer)) {
                List<GameProfile> players = HyStats.getOnlinePlayers().stream().map(NetworkPlayerInfo::getGameProfile).limit(24).collect(Collectors.toList());
                UChat.send(format(
                        "&7&oLoading stats of " + players.size() + " player" + (players.size() == 1 ? "" : "s") + ", please wait..."
                ));
                CompletableFuture.runAsync(() -> {
                    processor.mass().showPlayers(players);
                });
                return;
            }

            if (ImmutableList.of("-p", "-party", "--p", "--party").contains(requestedPlayer)) {
                send(format("&7&oChecking your party...", null, false));
                new CheckPartyList(processor);
                return;
            }

            if ((requestedPlayer.length() > 16
                    || !StringUtils.isAlphanumeric(requestedPlayer.replace("_", "")))
                    && !UUIDUtils.isUUID(requestedPlayer)
            ) {
                send(format("&cThat player does not exist: &8" + requestedPlayer, null, true)); return;
            }

            CompletableFuture.runAsync(() -> UChat.send(processor.processRequest(requestedPlayer)));
        } catch (Exception exception) {
            send(this.getError(exception, true));
        }
    }

    IChatComponent getError(Exception error, boolean showUtilityMessages) {
        error.printStackTrace(System.err);

        String hover = "&8" + error.toString();
        hover = hover.replaceAll("\\|E:", "\n&fError Code: &d");
        hover = hover + (error.getCause() != null ? "\n&fCause: &8" + error.getCause() : "");
        hover = hover + "\n\n&7&oCheck your logs for more information!";

        if (HyStatsError.class.isAssignableFrom(error.getClass())) {
            return format("&c" + error.getMessage().split("\\|E:")[0], hover, showUtilityMessages);
        }
        return format("&cAn error occurred while processing your request: &8" + error.toString().replace("\\|E:", ""), hover, showUtilityMessages);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 2) {
            return UTabCompletions.tab(args[1],
                    commands.stream().map(scc -> scc.name).collect(Collectors.toList())
            );
        }
        if (args.length == 1) {
            return UTabCompletions.tab(args[0],
                    HyStats.getOnlinePlayers().stream().map(npi -> npi.getGameProfile().getName()).collect(Collectors.toList())
            );
        }
        return null;
    }
}
