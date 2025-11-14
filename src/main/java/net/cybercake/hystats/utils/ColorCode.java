package net.cybercake.hystats.utils;

import net.minecraft.util.EnumChatFormatting;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum ColorCode {
    BLACK('0', 0x000000, true),       // Black
    DARK_BLUE('1', 0x0000AA, true),   // Dark Blue
    DARK_GREEN('2', 0x00AA00, true),  // Dark Green
    CYAN('3', 0x00AAAA, true),   // Dark Aqua
    DARK_AQUA('3', 0x00AAAA, true),   // Dark Aqua
    DARK_RED('4', 0xAA0000, true),    // Dark Red
    DARK_PURPLE('5', 0xAA00AA, true), // Dark Purple
    GOLD('6', 0xFFAA00, true),        // Gold
    GRAY('7', 0xAAAAAA, true),        // Gray
    DARK_GRAY('8', 0x555555, true),   // Dark Gray
    BLUE('9', 0x5555FF, true),        // Blue
    GREEN('a', 0x55FF55, true),       // Green
    LIME('a', 0x55FF55, true),       // Green
    AQUA('b', 0x55FFFF, true),        // Aqua
    RED('c', 0xFF5555, true),         // Red
    LIGHT_PURPLE('d', 0xFF55FF, true),// Light Purple
    PINK('d', 0xFF55FF, true),// Light Purple
    YELLOW('e', 0xFFFF55, true),      // Yellow
    WHITE('f', 0xFFFFFF, true),       // White

    BOLD('l', 0, false),
    ITALIC('o', 0, false),
    UNDERLINE('n', 0, false),
    OBFUSCATED('k', 0, false),
    STRIKETHROUGH('m', 0, false),
    RESET('r', 0, false);

    private final char code;
    private final int hex;
    private final boolean color;

    ColorCode(char code, int hex, boolean color) {
        this.code = code;
        this.hex = hex;
        this.color = color;
    }

    public char getCode() {
        return code;
    }
    public int getHex() {
        return hex;
    }
    public boolean isColor() { return color; }

    public EnumChatFormatting parseAsChatFormatting() {
        return EnumChatFormatting.getValueByName(this.name());
    }

    public static ColorCode getColorFromCode(char colorCode) {
        for (ColorCode color : values()) {
            if (color.code == colorCode) {
                return color;
            }
        }
        return WHITE; // Default to white
    }

    public static boolean hasColor(String text) {
        return getColor(text) != null;
    }

    public static ColorCode getColor(String text) {
        return Arrays.stream(ColorCode.values()).filter(c -> c.name().equalsIgnoreCase(text)).findFirst().orElse(null);
    }

    public static String stripColor(@Nullable String input) {
        if (input == null) {
            return null;
        }
        input = input.replace(getCharacter(), '&');
        return input.replaceAll("(?i)&[abcdef0-9klom]", "");
    }

    public static char getCharacter() {
        return '\u00A7';
    }
}