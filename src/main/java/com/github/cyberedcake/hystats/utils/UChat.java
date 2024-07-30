package com.github.cyberedcake.hystats.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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

    public static void send(String msg, @Nullable String hover, boolean hasSeparator) {
        send(UChat.chat(msg), hover == null ? null : UChat.chat(hover), hasSeparator);
    }

    public static void send(IChatComponent msg, IChatComponent hover, boolean hasSeparator) {
        if (hover != null)
            msg.setChatStyle(msg.getChatStyle().setChatHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)
            ));

        if (hasSeparator) {
            List<String> separator = new ArrayList<>();
            for (int i = 0; i < 80; i++) {
                separator.add("§9§m");
            }
            msg = UChat.chat(String.join(" ", separator))
                    .appendSibling(UChat.chat("\n"))
                    .appendSibling(msg)
                    .appendSibling(UChat.chat("\n"))
                    .appendSibling(UChat.chat(String.join(" ", separator)));
        }

        send(msg);
    }

    public static void send(IChatComponent msg) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(
                msg
        );
    }

}
