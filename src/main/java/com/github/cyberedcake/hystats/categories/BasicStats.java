package com.github.cyberedcake.hystats.categories;

import com.github.cyberedcake.hystats.command.GameStats;
import com.github.cyberedcake.hystats.command.StatsCategoryCommand;
import com.github.cyberedcake.hystats.utils.UChat;
import com.github.cyberedcake.hystats.utils.Time;
import com.github.cyberedcake.hystats.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;
import scala.collection.parallel.mutable.ParArray;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.github.cyberedcake.hystats.utils.Utils.formatDouble;

public class BasicStats extends StatsCategoryCommand {

    public BasicStats() {
        super("general", null, "View the basic stats from Hypixel for the player.", "basic");
    }


    @Override
    public void execute(ICommandSender sender, GameStats stats, boolean oneLine, String[] args) {
        String networkLevel = formatDouble(stats.player().getNetworkLevel());
        String achievementPoints = formatDouble(stats.getIntProperty("achievementPoints", 0));
        String karma = formatDouble(stats.player().getKarma());

        String firstLoginHover = "&6First Login:\n&f" + stats.player().getFirstLoginDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"));

        boolean online = stats.session().isOnline();
        String onlineStatus;
        String onlineStatusHover;
        ZonedDateTime unix;
        if (online) {
            onlineStatus = oneLine ? "&aON." : "&aOnline for ";
            unix = stats.player().getLastLoginDate();
            onlineStatusHover = "&6Online Since:\n&f" + unix.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"));
        } else {
            onlineStatus = oneLine ? "&cOFF." : "&cOffline for ";
            unix = stats.player().getLastLogoutDate();
            onlineStatusHover = "&6Offline Since:\n&f" + unix.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"));
        }

        if (oneLine) {

            send(stats.getUser());
            send("HL: &6" + networkLevel);
            send("AP: &e" + achievementPoints);
            send("Age: &2" + Time.getDuration(System.currentTimeMillis() / 1_000, stats.player().getFirstLoginDate().toEpochSecond(), false), firstLoginHover);

            if (unix.toEpochSecond() != 0)
                send(onlineStatus + " " + Time.getDuration(System.currentTimeMillis() / 1_000, unix.toEpochSecond(), false),
                        onlineStatusHover
                );
        } else {

            send("Stats of " + stats.getUser());
            send(" ");
            send("Hypixel Level: &6" + networkLevel);
            send("Achievement Points: &e" + achievementPoints);
            send("Karma: &d" + karma);
            send("First Login: &2" + Time.getDuration(System.currentTimeMillis() / 1_000, stats.player().getFirstLoginDate().toEpochSecond(), true) + " ago",
                    firstLoginHover);
            JsonObject object = stats.getObjectProperty("socialMedia.links");
            Map<String, String> map = new Gson().fromJson(object, new TypeToken<Map<String, String>>() {}.getType());
            if (object != null && !map.isEmpty()) {
                IChatComponent component = UChat.chat("Social Media: &7" + map.size() + " linked account" + (map.size() == 1 ? "" : "s") + " ... click to view!");
                component.getChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, UChat.chat("&eClick to view " + stats.getUser() + "&e'" + (stats.player().getName().endsWith("s") ? "" : "s") + " social media!")))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hystats " + stats.player().getName() + " socials"));
                send(component);
            }

            if (unix.toEpochSecond() != 0) {
                send(" ");
                send(onlineStatus + "&6" +
                                Time.getDuration(System.currentTimeMillis() / 1_000, unix.toEpochSecond(), true)
                                + (!stats.session().isOnline() || stats.session().getMode().equalsIgnoreCase("LOBBY") ? "" :
                                "&a, playing " + (stats.session().getServerType() == null ? "something mysterious" : stats.session().getServerType().getName())
                        )
                        , onlineStatusHover
                );
            }
        }
    }
}
