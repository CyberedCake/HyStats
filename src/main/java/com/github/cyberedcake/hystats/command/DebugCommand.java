package com.github.cyberedcake.hystats.command;

import com.github.cyberedcake.hystats.HyStatsMain;
import com.github.cyberedcake.hystats.hypixel.ranks.SpecialHypixelRank;
import com.github.cyberedcake.hystats.utils.UChat;
import com.github.cyberedcake.hystats.utils.UUIDGrabber;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DebugCommand extends CommandBase {

    static List<UUID> allowedUuids = new ArrayList<>();
    static {
        allowedUuids.add(UUID.fromString("98c87f21-bca9-4726-8b9e-e5cd2ef685f4")); // CyberedCake
        allowedUuids.add(UUID.fromString("64064df5-cb48-4fec-bb6f-b7aa7c33d94a")); // CyberedCake2
    }

    @Override
    public String getCommandName() {
        return "$statsdebug";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/$statsdebug";
    }

    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        try {
            if (args.length < 1) {
                send("&cMust provide a parameter!");
                return;
            }

            if (args[0].equalsIgnoreCase("--reload-api")) {
                if (HyStatsMain.API != null) {
                    send("&7&oShutting down API...");
                    HyStatsMain.shutdownApi();
                }

                HyStatsMain.loadApi();
                send("&aReloaded the Hypixel API!");
            } else if (args[0].equalsIgnoreCase("--reload-special-ranks")) {

                SpecialHypixelRank.createSpecialHypixelRanks();
                send("&aReloaded the special ranks!");

            } else if (args[0].equalsIgnoreCase("--send-raw")) {

                if (args.length < 2) {
                    send("&cMust provide text!");
                    return;
                }

                sender.addChatMessage(UChat.chat(String.join(" ", Arrays.copyOfRange(args, 1, args.length))));

            } else if (args[0].equalsIgnoreCase("--uuid-of")) {

                if (args.length < 2) {
                    send("&cMust provide username!");
                    return;
                }

                CompletableFuture.runAsync(() -> {
                    UUID uuid = UUIDGrabber.getUUIDOf(args[1]);
                    if (uuid == null) {
                        send("&cPlayer does not exist: " + args[1]);
                        return;
                    }

                    IChatComponent component = UChat.chat("&6&lHyStats Debugger: &f" + args[1] + " is " + uuid);
                    component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, UChat.chat("&eClick here to copy &b" + uuid + "&e (uuid of &a" + args[1] + "&e)!")));
                    component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, uuid.toString()));
                    UChat.send(component);
                });

            } else {
                send("&cInvalid parameter!");
            }
        } catch (Exception exception) {
            send("&cFailed to run debugger: &8" + exception);
            exception.printStackTrace();
        }
    }

    private void send(String msg) {
        UChat.send("&6&lHyStats Debugger: &f" + msg, null, false);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
//        if (!(sender instanceof EntityPlayer)) return true;
//        return allowedUuids.contains(((EntityPlayer) sender).getUniqueID());
        return true;
    }

}
