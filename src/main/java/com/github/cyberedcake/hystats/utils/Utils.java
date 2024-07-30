package com.github.cyberedcake.hystats.utils;

import net.hypixel.api.reply.PlayerReply;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;

public class Utils {

    public static String propertyOrNull(PlayerReply.Player player, String property) {
        return player.hasProperty(property) ? player.getProperty(property).getAsString() : "null";
    }

    public static String formatDouble(double d) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        formatter.setRoundingMode(RoundingMode.FLOOR);
        return formatter.format(d);
    }

    public static boolean isUuid(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

}
