package com.github.cyberedcake.hystats.categories;

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
        super("skywars", "skywars", "View a Hypixel user's SkyWars stats", "sw");
    }

    @Override
    public void execute(ICommandSender sender, String displayName, PlayerReply.Player player, StatusReply.Session session, String[] args) {
        String level = player.getStringProperty("stats.SkyWars.levelFormatted", "0✰");

        double wins = player.getDoubleProperty("stats.Bedwars.wins_bedwars", 0D);
        double losses = player.getDoubleProperty("stats.Bedwars.losses_bedwars", 0D);

        double finalKills = player.getDoubleProperty("stats.Bedwars.final_kills_bedwars", 0D);
        double finalDeaths = player.getDoubleProperty("stats.Bedwars.final_deaths_bedwars", 0D);

        int winstreak = player.getIntProperty("stats.Bedwars.winstreak", -1);

        send("Bed Wars Stats of " + displayName);
        send(" ");
        send("Level: &7" + BedWarsPrestige.format(star),
                "&6Prestige:\n&f" + Arrays.stream(prestige.name().toLowerCase().split("_"))
                        .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                        .collect(Collectors.joining(" "))
        );
        send("Wins/Losses: &2" + formatDouble(wins) + " &7/ &4" + formatDouble(losses) + " &f(WLR: &6" + formatDouble((wins / losses), "#,###.00") + "&f)");
        send("Final Kills/Deaths: &a" + formatDouble(finalKills) + " &7/ &c" + formatDouble(finalDeaths) + " &f(FKDR: &6" + formatDouble(finalKills / finalDeaths, "#,###.00") + "&f)");
        send("Kills: &e" + formatDouble(player.getIntProperty("stats.Bedwars.kills_bedwars", 0)));
        send("Coins: &6" + formatDouble(player.getIntProperty("stats.Bedwars.coins", 0)));
        send("Winstreak: &d" + (winstreak == -1 ? "&cDisabled" : winstreak), winstreak == -1 ? "&cThis user has disabled Winstreak visibility!" : null);
    }

    @Override
    public void oneLine(ICommandSender sender, String displayName, PlayerReply.Player player, StatusReply.Session session, String[] args) {

    }
}
