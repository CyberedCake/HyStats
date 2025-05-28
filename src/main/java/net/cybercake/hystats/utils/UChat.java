package net.cybercake.hystats.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UChat {

    private static final String PATTERN = "&([a-f0-9klon])";
    private static final String SEPARATOR = new String(new char[50]).replace("\0", "§9§m-");

    public static IChatComponent format(String message) {
        try {
            Pattern patternRegex = Pattern.compile(PATTERN);
            Matcher matcher = patternRegex.matcher(message);

            return new ChatComponentText(matcher.replaceAll("§$1"));
        } catch (Exception exception) {
            System.err.println("Failed to format text '" + message + "': " + exception);
            return new ChatComponentText(message);
        }
    }

    public static IChatComponent format(String message, @Nullable String hover, boolean separator) {
        return UChat.format(UChat.format(message), hover == null ? null : UChat.format(hover), separator);
    }

    public static IChatComponent format(IChatComponent msg, IChatComponent hover, boolean separator) {
        if (hover != null) {
            msg.setChatStyle(msg.getChatStyle().setChatHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)
            ));
        }

        if (separator) {
            msg = separator()
                    .appendSibling(UChat.format("\n"))
                    .appendSibling(msg)
                    .appendSibling(UChat.format("\n"))
                    .appendSibling(separator());
        }

        return msg;
    }

    public static IChatComponent separator() {
        return UChat.format(SEPARATOR);
    }

    public static void send(IChatComponent msg) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(msg);
    }

}
