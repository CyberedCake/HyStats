package com.github.cyberedcake.hystats.categories;

import com.github.cyberedcake.hystats.command.GameStats;
import com.github.cyberedcake.hystats.command.StatsCategoryCommand;
import com.github.cyberedcake.hystats.hypixel.BedWarsPrestige;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.github.cyberedcake.hystats.utils.Utils.formatDouble;

public class SkyWars extends StatsCategoryCommand {

    public SkyWars() {
        super("skywars", "stats.SkyWars", "View a Hypixel user's SkyWars statistics.", "sw");
    }

    @Override
    public void execute(ICommandSender sender, GameStats stats, boolean oneLine, String[] args) {
        String level = stats.getProperty("levelFormatted", "0✰");

        double wins = stats.getDoubleProperty("wins", 0D);
        double losses = stats.getDoubleProperty("losses", 0D);

        double kills = stats.getDoubleProperty("kills", 0D);
        double deaths = stats.getDoubleProperty("deaths", 0D);

        int winstreak = stats.getIntProperty("win_streak", -1);

        if (oneLine) {
            send(stats.getUser());
            send("Level: &7" + level);
            send("WLR: &e" + formatDouble(wins / losses, "#,###.00"), "&fWins/Losses:\n&2" + formatDouble(wins) + "&7/&4" + formatDouble(losses));
            send("KDR: &6" + formatDouble(kills / deaths, "#,###.00"), "&fKills/Deaths:\n&a" + formatDouble(kills) + "&7/&c" + formatDouble(deaths));
            send("WS: &d" + (winstreak == -1 ? "&cN/A" : "&d" + winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
            return;
        }

        send("SkyWars Stats of " + stats.getUser());
        send(" ");
        send("Level: &7" + level);
        send("Wins/Losses: &2" + formatDouble(wins) + " &7/ &4" + formatDouble(losses) + " &f(WLR: &6" + formatDouble((wins / losses), "#,###.00") + "&f)");
        send("Kills/Deaths: &a" + formatDouble(kills) + " &7/ &c" + formatDouble(deaths) + " &f(KDR: &6" + formatDouble(kills / deaths, "#,###.00") + "&f)");
        send("Souls: &b" + formatDouble(stats.getIntProperty("souls", 0)));
        send("Heads: &5" + formatDouble(stats.getIntProperty("heads", 0)));
        send("Coins/Tokens: &6" + formatDouble(stats.getIntProperty("coins", 0)) + " &7/ &2" + formatDouble(stats.getIntProperty("cosmetic_tokens", 0)));
        send("Winstreak: &d" + (winstreak == -1 ? "&cDisabled" : winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
    }
}
