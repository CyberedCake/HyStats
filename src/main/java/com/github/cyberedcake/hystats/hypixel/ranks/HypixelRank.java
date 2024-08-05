package com.github.cyberedcake.hystats.hypixel.ranks;

import com.github.cyberedcake.hystats.utils.ColorCode;
import net.hypixel.api.reply.PlayerReply;

import static com.github.cyberedcake.hystats.utils.Utils.propertyOrNull;

public enum HypixelRank {

    NONE("&7"),

    VIP("&a[VIP]"),

    VIP_PLUS("&a[VIP&6+&a]"),

    MVP("&b[MVP]"),

    MVP_PLUS("&b[MVP{1}+&b]"),

    MVP_PLUS_PLUS("{0}[MVP{1}++{0}]"),

    YOUTUBER("&c[&fYOUTUBE&c]"),

    GAME_MASTER("&2[GM]"),

    ADMIN("&c[ADMIN]"),

    CUSTOM((RankMeta) null);

    public static HypixelRank getRank(PlayerReply.Player player) {
        try {
            SpecialHypixelRank specialRank = SpecialHypixelRank.rankOf(player.getUuid());
            if (specialRank != null) {
                HypixelRank rank = CUSTOM;
                rank.meta = RankMeta.of(specialRank.getPrefix());
                return rank;
            }
            return HypixelRank.valueOf(player.getHighestRank().replace("SUPERSTAR", "MVP_PLUS_PLUS"));
        } catch (Exception exception) {
            System.out.println("An error occurred getting player rank of " + player.getName() + ": " + exception
                     + "\n" + "Highest Rank: " + player.getHighestRank() + " (value used to get " + HypixelRank.class.getCanonicalName() + ")"
                    + "\n" + "Rank: " + propertyOrNull(player, "rank")
                    + "\n" + "Monthly Package Rank: " + propertyOrNull(player, "monthlyPackageRank")
                    + "\n" + "Most Recent Monthly Package Rank: " + propertyOrNull(player, "mostRecentMonthlyPackageRank")
                    + "\n" + "New Package Rank: " + propertyOrNull(player, "newPackageRank")
                    + "\n" + "Rank Plus Color: " + propertyOrNull(player, "rankPlusColor")
                    + "\n" + "Monthly Rank Color: " + propertyOrNull(player, "monthlyRankColor")
            );
            return HypixelRank.NONE;
        }
    }

    private RankMeta meta;

    HypixelRank(String display) {
        this(RankMeta.of(display));
    }

    HypixelRank(RankMeta meta) {
        this.meta = meta;
    }

    public String format(PlayerReply.Player player) {
        String prefix = meta.getPrefix();
        if (prefix.contains("{1}")) {
            prefix = prefix.replace("{1}", "&" + ColorCode.valueOf(player.getStringProperty("rankPlusColor", "RED")).getCode());
        }
        if (prefix.contains("{0}")) {
            prefix = prefix.replace("{0}", player.hasProperty("monthlyRankColor") ? player.getStringProperty("monthlyRankColor", "GOLD").equalsIgnoreCase("AQUA") ? "&b" : "&6" : "&6");
        }

        return prefix + (this == HypixelRank.NONE ? "" : " ") + player.getName();
    }

}
