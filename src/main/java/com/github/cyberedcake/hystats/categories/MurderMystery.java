package com.github.cyberedcake.hystats.categories;

import com.github.cyberedcake.hystats.command.GameStats;
import com.github.cyberedcake.hystats.command.StatsCategoryCommand;
import com.github.cyberedcake.hystats.utils.Time;
import com.github.cyberedcake.hystats.utils.UChat;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.Nullable;

import static com.github.cyberedcake.hystats.utils.Utils.formatDouble;

public class MurderMystery extends StatsCategoryCommand {

    public MurderMystery() {
        super("murdermystery", "stats.MurderMystery", "View a Hypixel user's Murder Mystery statistics.", "mm", "murder");
    }

    @Override
    public void execute(ICommandSender sender, GameStats stats, boolean oneLine, String[] args) {
        double chanceDetective = stats.getDoubleProperty("detective_chance");
        double chanceMurderer = stats.getDoubleProperty("murderer_chance");
        double chanceAny = 100 - chanceMurderer - chanceDetective;

        double wins = stats.getDoubleProperty("wins", 0D);
        double winsDetective = stats.getDoubleProperty("detective_wins", 0D);
        double winsMurderer = stats.getDoubleProperty("murderer_wins", 0D);

        double kills = stats.getDoubleProperty("kills", 0D);
        double killsDetective = stats.getDoubleProperty("bow_kills", 0D);
        double killsMurderer = stats.getDoubleProperty("knife_kills", 0D);

        int quickestDetectiveWin = stats.getIntProperty("quickest_detective_win_time_seconds", -1);
        int quickestMurdererWin = stats.getIntProperty("quickest_murderer_win_time_seconds", -1);

        if (oneLine) {
            send(stats.getUser());
            send("&fWins: &a" + formatDouble(wins),
                    "&6&l&nWins:\n&eTotal: &f" + formatDouble(wins) + "\n&cAs Murderer: &f" +formatDouble(winsMurderer) + "\n&bAs Detective: &f" + formatDouble(winsDetective)
                    );
            send("&fKills: &c" + formatDouble(kills),
                    "&6&l&nKills:\n&eTotal: &f" + formatDouble(kills) + "\n&cAs Murderer: &f" + formatDouble(killsMurderer) + "\n&bAs Detective: &f" + formatDouble(killsDetective)
                    );
            send("&f&nTimes",
                    "&6&l&nFastest Wins:\n&cAs Murderer: &f" + Time.formatBasicSeconds(quickestMurdererWin) + "\n&bAs Detective: &f" + Time.formatBasicSeconds(quickestDetectiveWin)
                    );
            send("&f&nChances",
                    "&6&l&nChances:\n&aAs Innocent: &f" + formatDouble(chanceAny) + "%\n&cAs Murderer: &f" + formatDouble(chanceMurderer) + "%\n&bAs Detective: &f" + formatDouble(chanceDetective) + "%"
            );
            return;
        }

        send("Murder Mystery Stats of " + stats.getUser());
        send(" ");
        send(showAll("Chances", "&aAs Innocent::&a" + formatDouble(chanceAny) + "%", formatDouble(chanceMurderer) + "%", formatDouble(chanceDetective) + "%"));
        send(showAll("Wins", formatDouble(wins), formatDouble(winsMurderer), formatDouble(winsDetective)));
        send(showAll("Kills", formatDouble(kills), formatDouble(killsMurderer), formatDouble(killsDetective)));
        send(showAll("Fastest Win", null, Time.formatBasicSeconds(quickestMurdererWin), Time.formatBasicSeconds(quickestDetectiveWin)));
        send("&fTokens: &2" + formatDouble(stats.getIntProperty("coins", 0)));
    }

    private IChatComponent showAll(String title, @Nullable String total, String murderer, String detective) {
        IChatComponent component = UChat.chat("&f" + title + ": ");

        if (total != null) {
            component = component
                    .appendSibling(makeHover("&e" + (!total.contains("::") ? total : total.split("::")[1]), !total.contains("::") ? "&eTotal" : total.split("::")[0]))
                    .appendSibling(UChat.chat(" &7/ "))
            ;
        }

        component = component
                .appendSibling(makeHover("&c" + murderer, "&cAs Murderer"))
                .appendSibling(UChat.chat(" &7/ "))
                .appendSibling(makeHover("&b" + detective, "&bAs Detective"));

        return component;
    }

    private IChatComponent makeHover(String text, String hover) {
        IChatComponent returned = UChat.chat(text);
        returned.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, UChat.chat(hover)));
        return returned;
    }

}
