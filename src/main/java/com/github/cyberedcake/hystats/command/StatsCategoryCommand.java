package com.github.cyberedcake.hystats.command;

import com.github.cyberedcake.hystats.utils.UChat;
import net.hypixel.api.reply.PlayerReply;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class StatsCategoryCommand {

    public static IChatComponent SEPARATOR = null;

    public final String name;
    public final String usage;
    public final String description;
    public final String[] aliases;

    public StatsCategoryCommand(String name, String usage, String description, String... aliases) {
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(ICommandSender sender, PlayerReply.Player player, String[] args);

    public static void send(String msg) {
        send(msg, null);
    }

    public static void send(String msg, @Nullable String hover) {
        List<String> separator = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            separator.add("§9§m");
        }
        IChatComponent message = UChat.chat(msg);
        message.setChatStyle(message.getChatStyle().setChatHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, UChat.chat(hover))
        ));

        Minecraft.getMinecraft().thePlayer.addChatMessage(
                UChat.chat(String.join(" ", separator) + "\n").appendSibling(message).appendSibling(UChat.chat("\n" + String.join(" ", separator)))
        );
    }

}
