package net.cybercake.hystats.commands.stats.categories;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.cybercake.hystats.commands.flags.Arguments;
import net.cybercake.hystats.commands.stats.StatsCategoryCommand;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.ColorCode;
import net.cybercake.hystats.utils.Time;
import net.hypixel.api.reply.GuildReply;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static net.cybercake.hystats.utils.ApiUtils.formatDouble;
import static net.cybercake.hystats.utils.UChat.format;

public class BasicStats extends StatsCategoryCommand {

    public BasicStats() {
        super("general", null, "View the basic stats from Hypixel of the player.", "basic", "gen");
    }

    @Override
    public void execute(ICommandSender sender, GameStats stats, Arguments args, boolean compact) {
        String networkLevel = formatDouble(stats.player().getNetworkLevel());
        String achievementPoints = formatDouble(stats.getIntProperty("achievementPoints", 0));
        String karma = formatDouble(stats.player().getKarma());

        String firstLoginHover = "&6First Login:\n&f" + stats.player().getFirstLoginDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"));

        boolean online = stats.session().isOnline();
        String onlineStatus;
        String onlineStatusHover;
        ZonedDateTime unix;
        if (online) {
            onlineStatus = compact ? "&aON." : "&aOnline for ";
            unix = stats.player().getLastLoginDate();
            onlineStatusHover = "&6Online Since:\n&f" + unix.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"));
        } else {
            onlineStatus = compact ? "&cOFF." : "&cOffline for ";
            unix = stats.player().getLastLogoutDate();
            onlineStatusHover = "&6Offline Since:\n&f" + unix.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss z"));
        }

        if (compact) {

            text(stats.getUser(), "&eClick here to expand " + stats.getUser(), "/hystats " + stats.getUUID());
            text("HL: &6" + networkLevel);
            text("AP: &e" + achievementPoints);
            text("Age: &2" + Time.getDuration(System.currentTimeMillis() / 1_000, stats.player().getFirstLoginDate().toEpochSecond(), false), firstLoginHover);

            if (unix.toEpochSecond() != 0)
                text(onlineStatus + " " + Time.getDuration(System.currentTimeMillis() / 1_000, unix.toEpochSecond(), false),
                        onlineStatusHover
                );
        } else {

            text(format("Stats of ").appendSibling(stats.getUserWithGuild()));
            text(" ");
            text("Hypixel Level: &6" + networkLevel);
            text("Achievement Points: &e" + achievementPoints);
            text("Karma: &d" + karma);
            text("First Login: &2" + Time.getDuration(System.currentTimeMillis() / 1_000, stats.player().getFirstLoginDate().toEpochSecond(), true) + " ago",
                    firstLoginHover);
            JsonObject object = stats.getObjectProperty("socialMedia.links");
            Map<String, String> map = new Gson().fromJson(object, new TypeToken<Map<String, String>>() {
            }.getType());
            if (object != null && !map.isEmpty()) {
                IChatComponent component = format("Social Media: &7" + map.size() + " linked account" + (map.size() == 1 ? "" : "s") + " ... click to view!");
                component.getChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, format("&eClick to view " + stats.getUser() + "&e'" + (stats.player().getName().endsWith("s") ? "" : "s") + " social media!")))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hystats " + stats.getUUID() + " socials"));
                text(component);
            }

            if (unix.toEpochSecond() != 0) {
                text(" ");
                text(onlineStatus + "&6" +
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
