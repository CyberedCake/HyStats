package net.cybercake.hystats.commands;

import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.hypixel.exceptions.UserNotExistException;
import net.cybercake.hystats.hypixel.leveling.BedWarsPrestige;
import net.cybercake.hystats.hypixel.ranks.SpecialHypixelRank;
import net.cybercake.hystats.utils.UChat;
import net.cybercake.hystats.utils.UUIDUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static net.cybercake.hystats.utils.UChat.format;

public class DebugCommand extends CommandBase {

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
                HyStats.hypixel.reloadApi();
                send("&aReloaded the Hypixel API!");
            } else if (args[0].equalsIgnoreCase("--parse-bw-level")) {
                if (args.length < 2) {
                    send("&cMust provide number!");
                    return;
                }

                int level;
                try {
                    level = Integer.parseInt(args[1]);
                } catch (NumberFormatException exception) {
                    send("&cMust provide VALID integer!"); return;
                }

                BedWarsPrestige prestige = BedWarsPrestige.valueOf(level);
                send("Level: &7" + BedWarsPrestige.format(level),
                        "&6Prestige:\n&f" + Arrays.stream(prestige.name().toLowerCase().split("_"))
                                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                                .collect(Collectors.joining(" "))
                );
            } else if (args[0].equalsIgnoreCase("--reload-special-ranks")) {

                SpecialHypixelRank.createSpecialHypixelRanks();
                send("&aReloaded the special ranks!");

            } else if (args[0].equalsIgnoreCase("--send-raw")) {

                if (args.length < 2) {
                    send("&cMust provide text!");
                    return;
                }

                send((String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
            } else if (args[0].equalsIgnoreCase("--server")) {

                send(HyStats.getConnectedServer().toString());

            } else if (args[0].equalsIgnoreCase("--uuid-of")) {

                if (args.length < 2) {
                    send("&cMust provide username!");
                    return;
                }

                CompletableFuture.runAsync(() -> {
                    UUID uuid;
                    try {
                        uuid = UUIDUtils.processUUID(args[1]);
                    } catch (UserNotExistException err) {
                        send("&cPlayer does not exist: &8" + args[1]);
                        return;
                    }

                    IChatComponent component = format("&6&lHyStats Debugger: &f" + args[1] + " is " + uuid);
                    component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, format("&eClick here to copy &b" + uuid + "&e (uuid of &a" + args[1] + "&e)!")));
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
        UChat.send(format("&6&lHyStats Debugger: &f" + msg, null, false));
    }

    private void send(String msg, @Nullable String hover) {
        UChat.send(format("&6&lHyStats Debugger: &f" + msg, hover, false));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
