package com.github.cyberedcake.hystats.command;

import com.github.cyberedcake.hystats.ExampleMod;
import com.github.cyberedcake.hystats.categories.BasicStats;
import com.github.cyberedcake.hystats.categories.BedWars;
import com.github.cyberedcake.hystats.categories.Socials;
import com.github.cyberedcake.hystats.exceptions.NoHypixelPlayerException;
import com.github.cyberedcake.hystats.exceptions.UuidNotExist;
import com.github.cyberedcake.hystats.hypixel.CachedApiCall;
import com.github.cyberedcake.hystats.utils.UChat;
import com.github.cyberedcake.hystats.hypixel.HypixelRank;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;

public class StatsCommand extends CommandBase {

    private static final List<StatsCategoryCommand> commands = new ArrayList<>();
    private static StatsCategoryCommand noArgumentCommand;

    public StatsCommand() {
        commands.add(new BasicStats());
        commands.add(new Socials());
        commands.add(new BedWars());

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

    @SuppressWarnings("CallToPrintStackTrace")
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

            if (ExampleMod.API == null) {
                UChat.send("&cThe Hypixel API has been disabled and cannot continue.\n&7&oThis is likely due to a previous fatal error!", null, true);
                return;
            }

            if (player.length() > 16
                    || !StringUtils.isAlphanumeric(player.replace("_", ""))
            ) {
                UChat.send("&cThat player does not exist!", null, true); return;
            }

            if (!CachedApiCall.isCached(player))
                UChat.send("&7&oLoading stats, please wait...", null, false);

            StatsCategoryCommand finalCommand = command;
            CompletableFuture.runAsync(() -> {
                CachedApiCall api;
                try {
                    api = CachedApiCall.grab(player);
                } catch (UuidNotExist notExist) {
                    UChat.send("&cThat player does not exist!", "&cException:\n&8" + notExist, true);
                    return;
                } catch (NoHypixelPlayerException hypixelPlayerException) {
                    UChat.send("&cThat player has never logged onto Hypixel!", "&cException:\n&8" + hypixelPlayerException, true);
                    return;
                } catch (TimeoutException timeout) {
                    UChat.send("&cFailed to send request to Hypixel API: &8Request timed out!", "&cException:\n&8" + timeout, true);
                    timeout.printStackTrace();
                    return;
                } catch (RejectedExecutionException rejected) {
                    UChat.send("&cFailed to send request to Hypixel API: &8Execution rejected by " + rejected.getStackTrace()[0].getClassName(), "&cException (fatal):\n&8" + rejected, true);
                    rejected.printStackTrace();
                    System.out.println("Rejected, aborting!");
                    ExampleMod.shutdownApi();
                    return;
                } catch (Exception exception) {
                    UChat.send("&cFailed to send request to Hypixel API!", "&cException (fatal):\n&8" + exception, true);
                    exception.printStackTrace();
                    System.out.println("Since a Hypixel API failure occurred, shutting down API for future use...");
                    ExampleMod.shutdownApi();
                    return;
                }

                try {
                    IChatComponent text = new ChatComponentText("");
                    String display = HypixelRank.getRank(api.player).format(api.player);
                    if (showAll) {
                        finalCommand.execute(sender, display, api.player, api.session, args.length > 2 ? Arrays.copyOfRange(args, 1, args.length) : args);
                    } else {
                        finalCommand.oneLine(sender, display.replace("YOUTUBE", "YT"), api.player, api.session, args.length > 2 ? Arrays.copyOfRange(args, 1, args.length) : args);
                    }

                    int index = 0;
                    for (IChatComponent component : finalCommand.sentMessages) {
                        text = text.appendSibling(component);
                        if (index != finalCommand.sentMessages.size() - 1)
                            text = text.appendSibling(UChat.chat(showAll ? "\n" : " &8|&f "));
                        index++;
                    }
                    UChat.send(text, null, true);

                    finalCommand.sentMessages.clear();

                } catch (Exception exception) {
                    UChat.send("&cFailed to display requested data!", "&cException:\n&8" + exception, true);
                    exception.printStackTrace();
                }
            });



        } catch (Exception exception) {
            List<String> separator = new ArrayList<>();
            for (int i = 0; i < 80; i++) {
                separator.add("§4§m");
            }
            exception.printStackTrace();
            sender.addChatMessage(new ChatComponentText(String.join(" ", separator)));
            sender.addChatMessage(new ChatComponentText("§c§lAN ERROR OCCURRED!"));
            sender.addChatMessage(new ChatComponentText("§e-> §fWhile showing stats data"));
            sender.addChatMessage(new ChatComponentText("§e-> §fSpecific exception: §8" + exception));
            sender.addChatMessage(new ChatComponentText(" "));
            sender.addChatMessage(new ChatComponentText("§a§lPLEASE CREATE AN ISSUE REPORT!"));
            sender.addChatMessage(new ChatComponentText("§e-> §fGitHub: §bgithub.com/CyberedCake/HyStats"));
            sender.addChatMessage(new ChatComponentText("§e-> §f§nInclude your most recent log file!"));
            sender.addChatMessage(new ChatComponentText(String.join(" ", separator)));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
