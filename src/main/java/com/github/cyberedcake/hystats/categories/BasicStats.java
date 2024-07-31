package com.github.cyberedcake.hystats.categories;

import com.github.cyberedcake.hystats.command.StatsCategoryCommand;
import com.github.cyberedcake.hystats.utils.UChat;
import com.github.cyberedcake.hystats.utils.Time;
import com.github.cyberedcake.hystats.utils.Utils;
import com.google.gson.JsonObject;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BasicStats extends StatsCategoryCommand {

    public BasicStats() {
        super("general", "general", "View the basic stats from Hypixel for the player.", "basic");
    }


    @Override
    public void execute(ICommandSender sender, String display, PlayerReply.Player player, StatusReply.Session session, String[] args) {
        send("Stats of " + display);
        send(" ");
        send("Hypixel Level: &6" + Utils.formatDouble(player.getNetworkLevel()));
        send("Achievement Points: &e" + Utils.formatDouble(player.getIntProperty("achievementPoints", 0)));
        send("Karma: &d" + Utils.formatDouble(player.getKarma()));
        send("First Login: &2" + Time.getDuration(System.currentTimeMillis() / 1_000, player.getFirstLoginDate().toEpochSecond(), true) + " ago",
                "&6First Login:\n&f" + player.getFirstLoginDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"))
                );
        JsonObject object = player.getObjectProperty("socialMedia.links");
        if (object != null && !object.asMap().isEmpty()) {
            IChatComponent component = UChat.chat("Social Media: &7" + object.asMap().size() + " linked account" + (object.asMap().size() == 1 ? "" : "s") + " ... click to view!");
            component.getChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, UChat.chat("&eClick to view " + display + "&e'" + (player.getName().endsWith("s") ? "" : "s") + " social media!")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hystats " + player.getName() + " socials"));
            send(component);
        }

        String additional = "";
        String onlineStatus;
        ZonedDateTime unix;
        if (session.isOnline()) {
            additional = session.getMode().equalsIgnoreCase("LOBBY") ? "" : "&a, playing " + (session.getServerType() == null ? "something mysterious" : session.getServerType().getName());
            onlineStatus = "&aOnline for ";
            unix = player.getLastLoginDate();
        } else {
            onlineStatus = "&cOffline for ";
            unix = player.getLastLogoutDate();
        }

        if (unix.toEpochSecond() != 0) {
            send(" ");
            send(onlineStatus + "&6" +
                    Time.getDuration(System.currentTimeMillis() / 1_000, unix.toEpochSecond(), true)
                    + additional
                    ,
                     (session.isOnline() ? "&6Online Since:" : "&6Offline Since:")+ "\n&f" + unix.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"))
            );
        }
    }

    @Override
    public void oneLine(ICommandSender sender, String display, PlayerReply.Player player, StatusReply.Session session, String[] args) {
        send(display);
        send("HL: &6" + Utils.formatDouble(player.getNetworkLevel()));
        send("AP: &e" + Utils.formatDouble(player.getIntProperty("achievementPoints", 0)));
        send("Age: &2" + Time.getDuration(System.currentTimeMillis() / 1_000, player.getFirstLoginDate().toEpochSecond(), false),
                "&6First Login:\n&f" + player.getFirstLoginDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"))
                );

        String onlineStatus;
        ZonedDateTime unix;
        if (session.isOnline()) {
            onlineStatus = "&aON.";
            unix = player.getLastLoginDate();
        } else {
            onlineStatus = "&cOFF.";
            unix = player.getLastLogoutDate();
        }

        if (unix.toEpochSecond() != 0)
            send(onlineStatus + " " + Time.getDuration(System.currentTimeMillis() / 1_000, unix.toEpochSecond(), false),
                    (session.isOnline() ? "&6Online Since:" : "&6Offline Since:") + "\n&f" +
                            unix.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"))
                    );
    }
}
