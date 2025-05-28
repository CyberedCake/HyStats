package net.cybercake.hystats.commands.stats.categories;

import net.cybercake.hystats.commands.stats.StatsCategoryCommand;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.hypixel.leveling.BedWarsPrestige;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

import static net.cybercake.hystats.utils.ApiUtils.formatDouble;

public class BedWars extends StatsCategoryCommand {

    public BedWars() {
        super("bedwars", "stats.Bedwars", "View a Hypixel user's Bed Wars statistics.", "bw");
    }

    @Override
    public void execute(ICommandSender sender, GameStats stats, boolean compact) {
        int star = stats.player().getIntProperty("achievements.bedwars_level", 0);
        BedWarsPrestige prestige = BedWarsPrestige.valueOf(star);
        String starFormatted = BedWarsPrestige.format(star);
        String prestigeFormatted = Arrays.stream(prestige.name().toLowerCase().split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
        if (stats.getUUID().toString().equalsIgnoreCase("e8261512-53e9-469a-8015-6bd8bb896945")) {
            System.out.println("User is 1ist! We need the 1ist prestige!!!");
            starFormatted = "&8" + star + "&0✫";
            prestigeFormatted = "1ist";
        }

        double wins = stats.getDoubleProperty("wins_bedwars", 0D);
        double losses = stats.getDoubleProperty("losses_bedwars", 0D);

        double finalKills = stats.getDoubleProperty("final_kills_bedwars", 0D);
        double finalDeaths = stats.getDoubleProperty("final_deaths_bedwars", 0D);

        int winstreak = stats.getIntProperty("winstreak", -1);

        if (compact) {
            text(stats.getUser(), "&eClick here to expand " + stats.getUser(), "/hystats " + stats.getUUID() + " " + this.name);
            text("Star: &7" + starFormatted,
                    "&6Prestige:\n&f" + prestigeFormatted
            );
            text("WLR: &e" + formatDouble(wins / losses, "#,###.00"), "&fWins/Losses:\n&2" + formatDouble(wins) + "&7/&4" + formatDouble(losses));
            text("FKDR: &6" + formatDouble(finalKills / finalDeaths, "#,###.00"), "&fFinal Kills/Deaths:\n&a" + formatDouble(finalKills) + "&7/&c" + formatDouble(finalDeaths));
            text("WS: &d" + (winstreak == -1 ? "&cN/A" : "&d" + winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
            return;
        }

        text("Bed Wars Stats of " + stats.getUser());
        text(" ");
        text("Level: &7" + starFormatted,
                "&6Prestige:\n&f" + prestigeFormatted);
        text("Wins/Losses: &2" + formatDouble(wins) + " &7/ &4" + formatDouble(losses) + " &f(WLR: &6" + formatDouble((wins / losses), "#,###.00") + "&f)");
        text("Final Kills/Deaths: &a" + formatDouble(finalKills) + " &7/ &c" + formatDouble(finalDeaths) + " &f(FKDR: &6" + formatDouble(finalKills / finalDeaths, "#,###.00") + "&f)");
        text("Kills: &e" + formatDouble(stats.getIntProperty("kills_bedwars", 0)));
        text("Coins: &6" + formatDouble(stats.getIntProperty("coins", 0)));
        text("Winstreak: &d" + (winstreak == -1 ? "&cDisabled" : winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
    }
}
