package com.github.cyberedcake.hystats.command;

import com.github.cyberedcake.hystats.HyStatsMain;
import com.github.cyberedcake.hystats.categories.*;
import com.github.cyberedcake.hystats.exceptions.HyStatsError;
import com.github.cyberedcake.hystats.exceptions.NoHypixelPlayerException;
import com.github.cyberedcake.hystats.exceptions.NoUserException;
import com.github.cyberedcake.hystats.hypixel.CachedApiCall;
import com.github.cyberedcake.hystats.utils.UChat;
import com.github.cyberedcake.hystats.hypixel.ranks.HypixelRank;
import com.github.cyberedcake.hystats.utils.Utils;
import com.mojang.realmsclient.dto.PlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@SuppressWarnings("CallToPrintStackTrace")
public class StatsCommand extends CommandBase {

    private static final List<StatsCategoryCommand> commands = new ArrayList<>();
    private static StatsCategoryCommand noArgumentCommand;

    public StatsCommand() {
        commands.add(new BasicStats());
        commands.add(new Socials());
        commands.add(new BedWars());
        commands.add(new SkyWars());
        commands.add(new MurderMystery());

        noArgumentCommand = commands.stream().filter(cmd -> cmd.getClass() == BasicStats.class).findFirst().orElseThrow(() -> new RuntimeException("No basic stats command!"));
    }

    @Override
    public String getCommandName() {
        return "stats";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/stats <player> [game]";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>(Collections.singletonList("hystats"));
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        try {
            if (args.length < 1) {
                UChat.send("&cInvalid usage! &7" + this.getCommandUsage(sender), null, true); return;
            }

            String findingPlayer = args[0];
            final boolean showAll = !args[0].startsWith(":");
            findingPlayer = findingPlayer.replace(".", sender.getName());

            if (args[0].startsWith(":"))
                findingPlayer = findingPlayer.substring(1);

            final String player = findingPlayer;

            StatsCategoryCommand command = null;
            if (args.length == 1) command = noArgumentCommand;
            else {
                for (StatsCategoryCommand cmd : commands) {
                    if (!args[1].equalsIgnoreCase(cmd.name)
                            && !(cmd.aliases.length != 0 && Arrays.stream(cmd.aliases).anyMatch(s -> s.equalsIgnoreCase(args[1])))) continue;
                    command = cmd;
                }
            }

            if (command == null) {
                UChat.send("&cInvalid category: &8" + args[1] + "\n&cType &7/stats help &cfor help!", null, true);
                return;
            }

            if (HyStatsMain.API == null) {
                UChat.send("&cThe Hypixel API has been disabled and cannot continue.\n&7&oThis is likely due to a previous fatal error!", null, true);
                return;
            }

            if (player.equalsIgnoreCase("*")) {
                List<UUID> players = Minecraft.getMinecraft()
                        .getNetHandler()
                        .getPlayerInfoMap()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(npi -> !CachedApiCall.notExist.contains(npi.getGameProfile().getId()))
                        .limit(24)
                        .map(p -> p.getGameProfile().getId())
                        .collect(Collectors.toList());
                UChat.send("&7&oLoading stats of " + players.size() + " player(s)...", null, false);

                StatsCategoryCommand finalCommand = command;
                CompletableFuture.runAsync(() -> {
                    try {
                        List<CompletableFuture<IChatComponent>> futures = new ArrayList<>();

                        for (UUID uuid : players) {
                            CompletableFuture<IChatComponent> future = this.getStats(uuid.toString(), finalCommand, sender, !showAll, false, args)
                                    .thenApply((stats) -> {
                                        if (stats == null) {
                                            return null;
                                        }

                                        IChatComponent text = new ChatComponentText("");
                                        int textIndex = 0;
                                        for (IChatComponent c : stats) {
                                            text = text.appendSibling(c);
                                            if (textIndex != stats.size() - 1)
                                                text = text.appendSibling(UChat.chat(!showAll ? "\n" : " &8|&f "));
                                            textIndex++;
                                        }

                                        return text;
                                    });
                            if (future.isCompletedExceptionally()) continue;

                            futures.add(future);
                        }

                        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}));
                        all.join();

                        UChat.send(UChat.getSeparator(), null, false);
                        for (CompletableFuture<IChatComponent> futureComponent : futures) {
                            IChatComponent c = futureComponent.get();
                            if (c == null) continue;
                            UChat.send(c);
                        }
                        UChat.send(UChat.getSeparator(), null, false);

                        if (players.size() > 24) {
                            UChat.send("&c&oThere are " + (players.size() - 24) + " player(s) that can't be shown!", null, false);
                        }
                    } catch (Exception exception) {
                        error(sender, exception);
                    }
                });
                return;
            }

            if ((player.length() > 16
                    || !StringUtils.isAlphanumeric(player.replace("_", "")))
                    && !Utils.isUuid(player)
            ) {
                UChat.send("&cThat player does not exist!", null, true); return;
            }

            if (!CachedApiCall.isCached(player))
                UChat.send("&7&oLoading stats, please wait...", null, false);

            System.out.println("Loading stats for " + player + "...");
            getStats(player, command, sender, showAll, true, args).thenAcceptAsync((stats) -> {
                if (stats == null) {
                    return;
                }

                IChatComponent text = new ChatComponentText("");
                int index = 0;
                for (IChatComponent component : stats) {
                    text = text.appendSibling(component);
                    if (index != stats.size() - 1)
                        text = text.appendSibling(UChat.chat(showAll ? "\n" : " &8|&f "));
                    index++;
                }
                UChat.send(text, null, true);
            })
                    .exceptionally((exception) -> {
                        Class<?> e = exception.getClass();
                        if (e == NoUserException.class) {

                        } else {
                            UChat.send("&cFailed to send request to Hypixel API!", "&cException (fatal):\n&8" + exception, true);
                        }
                        return null;
                    })
            ;
        } catch (Exception exception) {
            error(sender, exception);
        }
    }

    private CompletableFuture<List<IChatComponent>> getStats(final String player, final StatsCategoryCommand command, final ICommandSender sender, final boolean showAll, final boolean separator, final String[] args) {
        return CompletableFuture.supplyAsync(() -> {
            CachedApiCall api;
            try {
                api = CachedApiCall.grab(player);
            } catch (Exception exception) {
                throw new HyStatsError(exception);
            }

            try {
                command.execute(sender,
                        new GameStats(
                                command.prefix,
                                api.player,
                                api.session,
                                HypixelRank.getRank(api.player).format(api.player)
                        ),
                        !showAll,
                        args);

                List<IChatComponent> returned = new ArrayList<>(command.sentMessages);
                command.sentMessages.clear();
                return returned;
            } catch (Exception exception) {
                UChat.send("&cFailed to display requested data!", "&cException:\n&8" + exception, separator);
                exception.printStackTrace();
                throw new IllegalStateException(exception.toString(), exception);
            }
        })
                .exceptionally(e -> {
                    UChat.send("&cFailed to execute HyStats command!", "&cException:\n&8" + e, separator);
                    e.printStackTrace();
                    return null;
                });
    }

    private void error(ICommandSender sender, Exception exception) {
        List<String> separator = new ArrayList<>();
        for (int i = 0; i < 53; i++) {
            separator.add("-");
        }
        exception.printStackTrace();
        sender.addChatMessage(new ChatComponentText("§4§m" + String.join("", separator)));
        sender.addChatMessage(new ChatComponentText("§c§lAN ERROR OCCURRED!"));
        sender.addChatMessage(new ChatComponentText("§e-> §fWhile showing stats data"));
        sender.addChatMessage(new ChatComponentText("§e-> §fSpecific exception: §8" + exception));
        sender.addChatMessage(new ChatComponentText(" "));
        sender.addChatMessage(new ChatComponentText("§a§lPLEASE CREATE AN ISSUE REPORT!"));
        sender.addChatMessage(new ChatComponentText("§e-> §fGitHub: §bgithub.com/CyberedCake/HyStats"));
        sender.addChatMessage(new ChatComponentText("§e-> §f§nInclude your most recent log file!"));
        sender.addChatMessage(new ChatComponentText("§4§m" + String.join("", separator)));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
