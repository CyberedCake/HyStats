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
        String level = stats.getProperty("LevelString", "levelFormatted", "0✰");
        String levelOnlyNumbers = level.replaceAll("[^0-9]", "");
        int levelInteger = Integer.parseInt(levelOnlyNumbers);
        stats.registerStat("Level", int.class, levelInteger);

        double wins = stats.getDoubleProperty("Wins", "wins", 0D);
        double losses = stats.getDoubleProperty("Losses", "losses", 0D);
        stats.registerStat("WLR", double.class, wins / losses);

        double kills = stats.getDoubleProperty("Kills", "kills", 0D);
        double deaths = stats.getDoubleProperty("Deaths", "deaths", 0D);
        stats.registerStat("KDR", double.class, kills / deaths);

        int winstreak = stats.getIntProperty("Winstreak", "win_streak", -1);

        int souls = stats.getIntProperty("Souls", "souls", 0);
        int heads = stats.getIntProperty("Heads", "heads", 0);
        int coins = stats.getIntProperty("Coins", "coins", 0);
        int tokens = stats.getIntProperty("Tokens", "cosmetic_tokens", 0);

        if (compact) {
            text(stats.getUser(), "&eClick here to expand " + stats.getUser(), "/hystats " + stats.getUUID() + " " + this.name);
            if (stats.isStaffStatsHidden().bool()) {
                text(HIDDEN_STATS);
                return;
            }
            text("Level: &7" + level);
            text("WLR: &e" + formatDouble(wins / losses, "#,###.00"), "&fWins/Losses:\n&2" + formatDouble(wins) + "&7/&4" + formatDouble(losses));
            text("KDR: &6" + formatDouble(kills / deaths, "#,###.00"), "&fKills/Deaths:\n&a" + formatDouble(kills) + "&7/&c" + formatDouble(deaths));
            text("WS: &d" + (winstreak == -1 ? "&cN/A" : "&d" + winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
            return;
        }

        text(format("SkyWars Stats of ").appendSibling(stats.getUserWithGuild()));
        if (stats.isStaffStatsHidden().bool()) {
            text(HIDDEN_STATS);
        }
        text(" ");
        text("Level: &7" + level);
        text("Wins/Losses: &2" + formatDouble(wins) + " &7/ &4" + formatDouble(losses) + " &f(WLR: &6" + formatDouble((wins / losses), "#,###.00") + "&f)");
        text("Kills/Deaths: &a" + formatDouble(kills) + " &7/ &c" + formatDouble(deaths) + " &f(KDR: &6" + formatDouble(kills / deaths, "#,###.00") + "&f)");
        text("Souls: &b" + formatDouble(souls));
        text("Heads: &5" + formatDouble(heads));
        text("Coins/Tokens: &6" + formatDouble(coins) + " &7/ &2" + formatDouble(tokens));
        text("Winstreak: &d" + (winstreak == -1 ? "&cDisabled" : winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
    }
}
