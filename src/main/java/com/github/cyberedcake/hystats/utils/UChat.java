package com.github.cyberedcake.hystats.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;
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
            msg = UChat.chat(getSeparator())
                    .appendSibling(UChat.chat("\n"))
                    .appendSibling(msg)
                    .appendSibling(UChat.chat("\n"))
                    .appendSibling(UChat.chat(getSeparator()));
        }

        send(msg);
    }

    public static @NotNull String getSeparator() {
        List<String> separator = new ArrayList<>();
//            int amount = (int) ((int) (Minecraft.getMinecraft().gameSettings.chatWidth * Minecraft.getMinecraft().fontRendererObj.getCharWidth('-')) * (52.22 / 6));
//            System.out.println("Amount of separators: " + amount);
//            System.out.println(Minecraft.getMinecraft().gameSettings.chatWidth + " * " + Minecraft.getMinecraft().fontRendererObj.getCharWidth('-') + " * (" + (52.22 / 6) + ")");
        for (int i = 0; i < 50; i++) {
            separator.add("-");
        }
        return "§9§m" + String.join("", separator);
    }

    public static void send(IChatComponent msg) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(
                msg
        );
    }

}
