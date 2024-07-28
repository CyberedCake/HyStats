package com.github.cyberedcake.hystats.command;

import com.github.cyberedcake.hystats.ExampleMod;
import com.github.cyberedcake.hystats.categories.BasicStats;
import com.github.cyberedcake.hystats.utils.UChat;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.PlayerReply;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import sun.rmi.runtime.Log;

import java.util.*;
import java.util.logging.Logger;

public class StatsCommand extends CommandBase {

    private static final List<StatsCategoryCommand> commands = new ArrayList<>();
    private static StatsCategoryCommand noArgumentCommand;

    public StatsCommand() {
        commands.add(new BasicStats());

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
        if (StatsCategoryCommand.SEPARATOR == null) {
            List<String> returned = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                returned.add("-");
            }
            StatsCategoryCommand.SEPARATOR = new ChatComponentText(String.join(" ", returned));
        }

        try {
            if (args.length < 1) {
                StatsCategoryCommand.send("&cInvalid usage! &7" + this.getCommandUsage(sender)); return;
            }

            String player = args[0];
            if (args[0].equalsIgnoreCase("me") || args[0].equalsIgnoreCase(".")) player = sender.getName();

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
                StatsCategoryCommand.send("&cInvalid category: &8" + args[1] + "\n&cType &7/stats help &cfor help!");
                return;
            }

            PlayerReply reply;
            try {
                UUID uuid = EntityPlayer.getOfflineUUID(args[0]);

                reply = ExampleMod.API.getPlayerByUuid(uuid).get();
            } catch (Exception exception) {
                StatsCategoryCommand.send("&cFailed to send request to Hypixel API!", "&c" + exception);
                exception.printStackTrace();
                return;
            }

            command.execute(sender, reply.getPlayer(), Arrays.copyOfRange(args, 1, args.length));



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
