package net.cybercake.hystats.utils;

import java.util.Arrays;

public enum ColorCode {
    BLACK('0', 0x000000),       // Black
    DARK_BLUE('1', 0x0000AA),   // Dark Blue
    DARK_GREEN('2', 0x00AA00),  // Dark Green
    CYAN('3', 0x00AAAA),   // Dark Aqua
    DARK_AQUA('3', 0x00AAAA),   // Dark Aqua
    DARK_RED('4', 0xAA0000),    // Dark Red
    DARK_PURPLE('5', 0xAA00AA), // Dark Purple
    GOLD('6', 0xFFAA00),        // Gold
    GRAY('7', 0xAAAAAA),        // Gray
    DARK_GRAY('8', 0x555555),   // Dark Gray
    BLUE('9', 0x5555FF),        // Blue
    GREEN('a', 0x55FF55),       // Green
    LIME('a', 0x55FF55),       // Green
    AQUA('b', 0x55FFFF),        // Aqua
    RED('c', 0xFF5555),         // Red
    LIGHT_PURPLE('d', 0xFF55FF),// Light Purple
    PINK('d', 0xFF55FF),// Light Purple
    YELLOW('e', 0xFFFF55),      // Yellow
    WHITE('f', 0xFFFFFF);       // White

    private final char code;
    private final int hex;

    ColorCode(char code, int hex) {
        this.code = code;
        this.hex = hex;
    }

    public char getCode() {
        return code;
    }

    public int getHex() {
        return hex;
    }

    public static int getColorFromCode(char colorCode) {
        for (ColorCode color : values()) {
            if (color.code == colorCode) {
                return color.hex;
            }
        }
        return WHITE.hex; // Default to white
    }

    public static boolean hasColor(String text) {
        return getColor(text) != null;
    }

    public static ColorCode getColor(String text) {
        return Arrays.stream(ColorCode.values()).filter(c -> c.name().equalsIgnoreCase(text)).findFirst().orElse(null);
    }
}