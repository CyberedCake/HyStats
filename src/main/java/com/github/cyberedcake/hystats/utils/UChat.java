package com.github.cyberedcake.hystats.utils;

import net.minecraft.util.ChatComponentText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UChat {

    public static String pattern = "&([a-f0-9klon])";

    public static ChatComponentText chat(String message) {
        try {
            Pattern patternRegex = Pattern.compile(pattern);
            Matcher matcher = patternRegex.matcher(message);

            return new ChatComponentText(matcher.replaceAll("§$1"));
        } catch (Exception exception) {
            System.out.println("Failed to format text '" + message + "': " + exception);
            return new ChatComponentText(message);
        }
    }

}
