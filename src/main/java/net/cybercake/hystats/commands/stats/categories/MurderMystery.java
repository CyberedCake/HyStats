package net.cybercake.hystats.commands.stats.categories;

import net.cybercake.hystats.commands.flags.Arguments;
import net.cybercake.hystats.commands.stats.StatsCategoryCommand;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.Time;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;

import static net.cybercake.hystats.utils.ApiUtils.formatDouble;
import static net.cybercake.hystats.utils.UChat.format;

public class MurderMystery extends StatsCategoryCommand {

    public MurderMystery() {
        super("murdermystery", "stats.MurderMystery", "View a Hypixel user's Murder Mystery statistics.", "mm", "murder");
    }

    @Override
    public void execute(ICommandSender sender, GameStats stats, Arguments args, boolean compact) {
        double chanceDetective = stats.getDoubleProperty("DetectiveChance", "detective_chance");
        double chanceMurderer = stats.getDoubleProperty("MurdererChance", "murderer_chance");
        double chanceAny = 100 - chanceMurderer - chanceDetective;

        double wins = stats.getDoubleProperty("Wins", "wins", 0D);
        double winsDetective = stats.getDoubleProperty("DetectiveWins", "detective_wins", 0D);
        double winsMurderer = stats.getDoubleProperty("MurdererWins", "murderer_wins", 0D);

        double kills = stats.getDoubleProperty("Kills", "kills", 0D);
        double killsDetective = stats.getDoubleProperty("BowKills", "bow_kills", 0D);
        double killsMurderer = stats.getDoubleProperty("KnifeKills", "knife_kills", 0D);

        int quickestDetectiveWin = stats.getIntProperty("QuickestDetectiveWin", "quickest_detective_win_time_seconds", -1);
        int quickestMurdererWin = stats.getIntProperty("QuickestMurdererWin", "quickest_murderer_win_time_seconds", -1);

        int coins = stats.getIntProperty("Coins", "coins", 0);

        if (compact) {
            text(stats.getUser(), "&eClick here to expand " + stats.getUser(), "/hystats " + stats.getUUID() + " " + this.name);
            if (stats.isStaffStatsHidden().bool()) {
                text(HIDDEN_STATS);
                return;
            }
            text("&fWins: &a" + formatDouble(wins),
                    "&6&l&nWins:\n&eTotal: &f" + formatDouble(wins) + "\n&cAs Murderer: &f" +formatDouble(winsMurderer) + "\n&bAs Detective: &f" + formatDouble(winsDetective)
            );
            text("&fKills: &c" + formatDouble(kills),
                    "&6&l&nKills:\n&eTotal: &f" + formatDouble(kills) + "\n&cAs Murderer: &f" + formatDouble(killsMurderer) + "\n&bAs Detective: &f" + formatDouble(killsDetective)
            );
            text("&f&nTimes",
                    "&6&l&nFastest Wins:\n&cAs Murderer: &f" + Time.formatBasicSeconds(quickestMurdererWin) + "\n&bAs Detective: &f" + Time.formatBasicSeconds(quickestDetectiveWin)
            );
            text("&f&nChances",
                    "&6&l&nChances:\n&aAs Innocent: &f" + formatDouble(chanceAny) + "%\n&cAs Murderer: &f" + formatDouble(chanceMurderer) + "%\n&bAs Detective: &f" + formatDouble(chanceDetective) + "%"
            );
            return;
        }

        text(format("Murder Mystery Stats of ").appendSibling(stats.getUserWithGuild()));
        if (stats.isStaffStatsHidden().bool()) {
            text(HIDDEN_STATS);
        }
        text(" ");
        text(showAll("Chances", "&aAs Innocent::&a" + formatDouble(chanceAny) + "%", formatDouble(chanceMurderer) + "%", formatDouble(chanceDetective) + "%"));
        text(showAll("Wins", formatDouble(wins), formatDouble(winsMurderer), formatDouble(winsDetective)));
        text(showAll("Kills", formatDouble(kills), formatDouble(killsMurderer), formatDouble(killsDetective)));
        text(showAll("Fastest Win", null, Time.formatBasicSeconds(quickestMurdererWin), Time.formatBasicSeconds(quickestDetectiveWin)));
        text("&fTokens: &2" + formatDouble(coins));
    }

    private IChatComponent showAll(String title, @Nullable String total, String murderer, String detective) {
        IChatComponent component = format("&f" + title + ": ");

        if (total != null) {
            component = component
                    .appendSibling(makeHover("&e" + (!total.contains("::") ? total : total.split("::")[1]), !total.contains("::") ? "&eTotal" : total.split("::")[0]))
                    .appendSibling(format(" &7/ "))
            ;
        }

        component = component
                .appendSibling(makeHover("&c" + murderer, "&cAs Murderer"))
                .appendSibling(format(" &7/ "))
                .appendSibling(makeHover("&b" + detective, "&bAs Detective"));

        return component;
    }

    private IChatComponent makeHover(String text, String hover) {
        IChatComponent returned = format(text);
        returned.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, format(hover)));
        return returned;
    }

}
