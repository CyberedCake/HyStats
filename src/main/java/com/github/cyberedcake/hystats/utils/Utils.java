package com.github.cyberedcake.hystats.utils;

import net.hypixel.api.reply.PlayerReply;

import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.UUID;

public class Utils {

    public static String propertyOrNull(PlayerReply.Player player, String property) {
        return player.hasProperty(property) ? player.getProperty(property).getAsString() : "null";
    }

    public static String formatDouble(double d, String pattern) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            return "&cN/A";
        }
        DecimalFormat formatter = new DecimalFormat(pattern);
        formatter.setRoundingMode(RoundingMode.FLOOR);
        return formatter.format(d);
    }

    public static String formatDouble(double d) {
        return formatDouble(d, "#,###");
    }

    public static boolean isUuid(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public static boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException exception) {
            return false;
        }
    }

}
