package net.cybercake.hystats.commands;

import com.google.common.collect.ImmutableList;
import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.hypixel.exceptions.UserNotExistException;
import net.cybercake.hystats.hypixel.leveling.BedWarsPrestige;
import net.cybercake.hystats.hypixel.ranks.HypixelRank;
import net.cybercake.hystats.hypixel.ranks.SpecialHypixelRank;
import net.cybercake.hystats.utils.UChat;
import net.cybercake.hystats.utils.UTabCompletions;
import net.cybercake.hystats.utils.UUIDUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

            if (args[0].equalsIgnoreCase("reloadapi")) {
                HyStats.hypixel.reloadApi();
                send("&aReloaded the Hypixel API and invalidated all caches!");
            } else if (args[0].equalsIgnoreCase("bwlevel")) {
                if (args.length < 3) {
                    int page;
                    try {
                        page = args.length < 2 ? 0 : (args[1].contains("page=") ? Integer.parseInt(args[1].replace("page=", "")) : -1);
                    } catch (Exception exception) {
                        page = -1;
                    }

                    int VALUES_PER_PAGE = 5;
                    if (page >= 0) {
                        int min = VALUES_PER_PAGE * page;
                        int max = Math.min(BedWarsPrestige.values().length, VALUES_PER_PAGE * page + VALUES_PER_PAGE);
                        List<BedWarsPrestige> prestiges = Arrays.stream(BedWarsPrestige.values())
                                .collect(Collectors.toList())
                                .subList(
                                        min, /* to */ max
                                );

                        send("Bed Wars Prestiges: (Page " + page + ")");
                        send("&7&o" + getCommandUsage(sender) + " bwlevel <star>");
                        for (BedWarsPrestige prestige : prestiges) {
                            try {
                                UChat.send(format(prestige.format(100 * prestige.ordinal()) + " (" + prestige.nameFormatted() + " Prestige)"));
                            } catch (Exception exception) {
                                System.err.println("Error: " + exception + " (" + prestige + ")");
                                UChat.send(format(" "));
                            }
                        }

                        IChatComponent prev = UChat.format((page == 0 ? "&7" : "&f") + "< PREV");
                        prev.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, format(
                                (page == 0 ? "&8" : "&e") + "Previous page")));
                        prev.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, this.getCommandUsage(sender) + " bwlevel page=" + (page - 1)));

                        boolean nextPageDoesntExist = VALUES_PER_PAGE * page >= BedWarsPrestige.values().length;
                        IChatComponent next = UChat.format((nextPageDoesntExist ? "&7" : "&f") + "NEXT >");
                        next.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, format(
                                (nextPageDoesntExist ? "&8" : "&e") + "Next page")));
                        next.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, this.getCommandUsage(sender) + " bwlevel page=" + (page + 1)));

                        UChat.send(format(" "));
                        UChat.send(prev.appendSibling(format(" &8| ")).appendSibling(next));
                        return;
                    }
                }

                int level;
                try {
                    level = Math.abs(Integer.parseInt(args[1]));
                } catch (NumberFormatException exception) {
                    send("&cMust provide VALID integer!"); return;
                }

                BedWarsPrestige prestige = BedWarsPrestige.valueOf(level);
                send("Level: &7" + BedWarsPrestige.findAndFormat(level),
                        "&6Prestige:\n&f" + Arrays.stream(prestige.name().toLowerCase().split("_"))
                                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                                .collect(Collectors.joining(" "))
                );
            } else if (args[0].equalsIgnoreCase("reloadspecialranks")) {

                SpecialHypixelRank.createSpecialHypixelRanks();
                send("&aReloaded the special ranks!");

            } else if (args[0].equalsIgnoreCase("sendraw")) {

                if (args.length < 2) {
                    send("&cMust provide text!");
                    return;
                }

                send(" ");
                UChat.send(format(String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
            } else if (args[0].equalsIgnoreCase("server")) {

                send(HyStats.getConnectedServer().toString());

            } else if (args[0].equalsIgnoreCase("uuid")) {

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
        UChat.send(format("&6&lHyStats Debug: &f" + msg, null, false));
    }

    private void send(String msg, @Nullable String hover) {
        UChat.send(format("&6&lHyStats Debug: &f" + msg, hover, false));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return UTabCompletions.tab(args[0],
                    new ArrayList<>(ImmutableList.of("reloadapi", "bwlevel", "reloadspecialranks", "sendraw", "server", "uuid"))
            );
        }
        return null;
    }
}
