package com.github.cyberedcake.hystats.categories;

import com.github.cyberedcake.hystats.command.GameStats;
import com.github.cyberedcake.hystats.command.StatsCategoryCommand;
import com.github.cyberedcake.hystats.hypixel.BedWarsPrestige;
import com.github.cyberedcake.hystats.utils.Utils;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.github.cyberedcake.hystats.utils.Utils.formatDouble;

public class BedWars extends StatsCategoryCommand {

    public BedWars() {
        super("bedwars", "stats.Bedwars", "View a Hypixel user's Bed Wars statistics.", "bw");
    }

    @Override
    public void execute(ICommandSender sender, GameStats stats, boolean oneLine, String[] args) {
        int star = stats.player().getIntProperty("achievements.bedwars_level", 0);
        BedWarsPrestige prestige = BedWarsPrestige.valueOf(star);

        double wins = stats.getDoubleProperty("wins_bedwars", 0D);
        double losses = stats.getDoubleProperty("losses_bedwars", 0D);

        double finalKills = stats.getDoubleProperty("final_kills_bedwars", 0D);
        double finalDeaths = stats.getDoubleProperty("final_deaths_bedwars", 0D);

        int winstreak = stats.getIntProperty("winstreak", -1);

        if (oneLine) {
            send(stats.getUser());
            send("Star: &7" + BedWarsPrestige.format(star),
                    "&6Prestige:\n&f" + Arrays.stream(prestige.name().toLowerCase().split("_"))
                            .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                            .collect(Collectors.joining(" "))
            );
            send("WLR: &e" + formatDouble(wins / losses, "#,###.00"), "&fWins/Losses:\n&2" + formatDouble(wins) + "&7/&4" + formatDouble(losses));
            send("FKDR: &6" + formatDouble(finalKills / finalDeaths, "#,###.00"), "&fFinal Kills/Deaths:\n&a" + formatDouble(finalKills) + "&7/&c" + formatDouble(finalDeaths));
            send("WS: &d" + (winstreak == -1 ? "&cN/A" : "&d" + winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
            return;
        }

        send("Bed Wars Stats of " + stats.getUser());
        send(" ");
        send("Level: &7" + BedWarsPrestige.format(star),
                "&6Prestige:\n&f" + Arrays.stream(prestige.name().toLowerCase().split("_"))
                        .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                        .collect(Collectors.joining(" "))
                );
        send("Wins/Losses: &2" + formatDouble(wins) + " &7/ &4" + formatDouble(losses) + " &f(WLR: &6" + formatDouble((wins / losses), "#,###.00") + "&f)");
        send("Final Kills/Deaths: &a" + formatDouble(finalKills) + " &7/ &c" + formatDouble(finalDeaths) + " &f(FKDR: &6" + formatDouble(finalKills / finalDeaths, "#,###.00") + "&f)");
        send("Kills: &e" + formatDouble(stats.getIntProperty("kills_bedwars", 0)));
        send("Coins: &6" + formatDouble(stats.getIntProperty("coins", 0)));
        send("Winstreak: &d" + (winstreak == -1 ? "&cDisabled" : winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
    }
}
