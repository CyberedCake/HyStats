package net.cybercake.hystats.hypixel.ranks;

import net.cybercake.hystats.utils.ColorCode;
import net.hypixel.api.reply.PlayerReply;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.cybercake.hystats.utils.ApiUtils.propertyOrNull;

public enum HypixelRank {

    NONE("&7"),

    VIP("&a[VIP]", (player, prefix) -> prefix + " "),

    VIP_PLUS("&a[VIP&6+&a]", (player, prefix) -> prefix + " "),

    MVP("&b[MVP]", (player, prefix) -> prefix + " "),

    MVP_PLUS("&b[MVP{plus_color}+&b]", (player, prefix) ->
            prefix
                    .replace("{plus_color}", "&" + ColorCode.valueOf(player.getStringProperty("rankPlusColor", "RED")).getCode())
                    + " "),

    MVP_PLUS_PLUS("{rank_color}[MVP{plus_color}++{rank_color}]", (player, prefix) ->
            prefix
                    .replace("{plus_color}", "&" + ColorCode.valueOf(player.getStringProperty("rankPlusColor", "RED")).getCode())
                    .replace("{rank_color}", player.hasProperty("monthlyRankColor") ? player.getStringProperty("monthlyRankColor", "GOLD").equalsIgnoreCase("AQUA") ? "&b" : "&6" : "&6")
                    + " "),

    YOUTUBER("&c[&fYOUTUBE&c]", (player, prefix) -> prefix + " "),

    STAFF("&c[&6ዞ&c]", (player, prefix) -> prefix + " "),

    CUSTOM((RankMeta) null);

    public static HypixelRank getRank(PlayerReply.Player player) {
        try {
            SpecialHypixelRank specialRank = SpecialHypixelRank.rankOf(player.getUuid());
            if (specialRank != null) {
                HypixelRank rank = CUSTOM;
                rank.meta = new RankMeta(specialRank.getPrefix());
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
            HypixelRank err = HypixelRank.CUSTOM;
            err.meta = new RankMeta("&8[???] &7");
            return err;
        }
    }

    private RankMeta meta;
    private @Nullable BiFunction<PlayerReply.Player, String, String> apply;

    HypixelRank(String display) {
        this(display, null);
    }

    HypixelRank(String display, @Nullable BiFunction<PlayerReply.Player, String, String> apply) {
        this(new RankMeta(display));
        this.apply = apply;
    }

    HypixelRank(RankMeta meta) {
        this.meta = meta;
    }

    public String format(PlayerReply.Player player) {
        String prefix = meta.prefix;
        if (apply != null) {
            prefix = this.apply.apply(player, prefix);
        }
        return prefix + player.getName();
    }

}
