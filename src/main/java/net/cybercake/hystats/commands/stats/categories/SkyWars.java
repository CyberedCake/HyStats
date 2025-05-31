package net.cybercake.hystats.commands.stats.categories;

import net.cybercake.hystats.commands.flags.Arguments;
import net.cybercake.hystats.commands.stats.StatsCategoryCommand;
import net.cybercake.hystats.hypixel.GameStats;
import net.minecraft.command.ICommandSender;

import static net.cybercake.hystats.utils.ApiUtils.formatDouble;
import static net.cybercake.hystats.utils.UChat.format;

public class SkyWars extends StatsCategoryCommand {

    public SkyWars() {
        super("skywars", "stats.SkyWars", "View a Hypixel user's SkyWars statistics.", "sw");
    }

    @Override
    public void execute(ICommandSender sender, GameStats stats, Arguments args, boolean compact) {
        String level = stats.getProperty("levelFormatted", "0✰");

        double wins = stats.getDoubleProperty("wins", 0D);
        double losses = stats.getDoubleProperty("losses", 0D);

        double kills = stats.getDoubleProperty("kills", 0D);
        double deaths = stats.getDoubleProperty("deaths", 0D);

        int winstreak = stats.getIntProperty("win_streak", -1);

        if (compact) {
            text(stats.getUser(), "&eClick here to expand " + stats.getUser(), "/hystats " + stats.getUUID() + " " + this.name);
            text("Level: &7" + level);
            text("WLR: &e" + formatDouble(wins / losses, "#,###.00"), "&fWins/Losses:\n&2" + formatDouble(wins) + "&7/&4" + formatDouble(losses));
            text("KDR: &6" + formatDouble(kills / deaths, "#,###.00"), "&fKills/Deaths:\n&a" + formatDouble(kills) + "&7/&c" + formatDouble(deaths));
            text("WS: &d" + (winstreak == -1 ? "&cN/A" : "&d" + winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
            return;
        }

        text(format("SkyWars Stats of ").appendSibling(stats.getUserWithGuild()));
        text(" ");
        text("Level: &7" + level);
        text("Wins/Losses: &2" + formatDouble(wins) + " &7/ &4" + formatDouble(losses) + " &f(WLR: &6" + formatDouble((wins / losses), "#,###.00") + "&f)");
        text("Kills/Deaths: &a" + formatDouble(kills) + " &7/ &c" + formatDouble(deaths) + " &f(KDR: &6" + formatDouble(kills / deaths, "#,###.00") + "&f)");
        text("Souls: &b" + formatDouble(stats.getIntProperty("souls", 0)));
        text("Heads: &5" + formatDouble(stats.getIntProperty("heads", 0)));
        text("Coins/Tokens: &6" + formatDouble(stats.getIntProperty("coins", 0)) + " &7/ &2" + formatDouble(stats.getIntProperty("cosmetic_tokens", 0)));
        text("Winstreak: &d" + (winstreak == -1 ? "&cDisabled" : winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
    }
}
