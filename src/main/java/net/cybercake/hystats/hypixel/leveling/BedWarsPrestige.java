package net.cybercake.hystats.hypixel.leveling;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum BedWarsPrestige {

    NONE("&7#✫"),
    IRON("&f###✫"),
    GOLD("&6###✫"),
    DIAMOND("&b###✫"),
    EMERALD("&2###✫"),
    SAPPHIRE("&3###✫"),
    RUBY("&4###✫"),
    CRYSTAL("&d###✫"),
    OPAL("&9###✫"),
    AMETHYST("&5###✫"),

    RAINBOW("&6#&e#&a#&b#&d✫"),
    IRON_PRIME("&f####&7✪"),
    GOLD_PRIME("&e####&6✪"),
    DIAMOND_PRIME("&b####&3✪"),
    EMERALD_PRIME("&a####&2✪"),
    SAPPHIRE_PRIME("&3####&9✪"),
    RUBY_PRIME("&c####&4✪"),
    CRYSTAL_PRIME("&d####&5✪"),
    OPAL_PRIME("&9####&1✪"),
    AMETHYST_PRIME("&5####&8✪"),

    MIRROR("&7#&f00&70✪"),
    LIGHT("&f#&e##&6#⚝"),
    DAWN("&6#&f##&b#⚝"),
    DUSK("&5#&d##&6#&e⚝"),
    AIR("&b#&f##&7#⚝"),
    WIND("&f#&a##&2#⚝"),
    NEBULA("&4#&c##&d#⚝"),
    THUNDER("&e#&f##&8#⚝"),
    EARTH("&a#&2##&6#⚝"),
    WATER("&b#&3##&9#⚝"),
    FIRE("&e#&6##&c#⚝"),

    SUNRISE("&9#&3##&6#✥"),
    ECLIPSE("&4#&8##&4#&c✥"),
    GAMMA("&9##&d#&c#✥"),
    MAJESTIC("&2#&a#&d##&5✥"),
    ANDESINE("&c#&4##&5#&a✥"),
    MARINE("&a##&b#&9#✥"),
    ELEMENT("&4#&c##&b#&3✥"),
    GALAXY("&1#&9##&5#✥"),
    ATOMIC("&4#&2##&3#&9✥"),

    SUNSET("&5#&c##&6#✥"),
    TIME("&e#&6#&c#&d#✥"),
    WINTER("&9#&3#&b#&f#&7✥"),
    OBSIDIAN("&5#&8##&5#✥"),
    SPRING("&2#&a#&e#&6#&5✥"),
    ICE("&f#&b##&3#✥"),
    SUMMER("&b#&e##&6#&d✥"),
    SPINEL("&4#&c##&9#&1✥"),
    AUTUMN("&5#&c#&6#&e#&b✥"),
    MYSTIC("&a#&f##&a#✥"),
    ETERNAL("&4#&5#&9##&1✥"),

    CUSTOM(null);

    final int min;
    final int max;
    final String display;

    BedWarsPrestige(String display) {
        this.min = (this.ordinal() * 100);
        this.max = ((this.ordinal() + 1) * 100) - 1;
        this.display = display;
    }

    public static BedWarsPrestige valueOf(int star) {
        if (star >= 5100) return BedWarsPrestige.ETERNAL;

        return Arrays.stream(BedWarsPrestige.values()).filter(p -> star >= p.min && star <= p.max).findFirst().orElse(BedWarsPrestige.NONE);
    }

    public static String findAndFormat(int star) {
        // find
        BedWarsPrestige prestige = valueOf(star);
        if (prestige == BedWarsPrestige.NONE) return prestige.display.replace("#", String.valueOf(star));

        // format
        return prestige.format(star);
    }

    public String format(int star) {
        String formatted = this.display;
        for (char digit : String.valueOf(star).toCharArray()) {
            formatted = formatted.replaceFirst("#", String.valueOf(digit));
        }
        return formatted;
    }

    public String nameFormatted() {
        return Arrays.stream(this.name().toLowerCase().split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

}
