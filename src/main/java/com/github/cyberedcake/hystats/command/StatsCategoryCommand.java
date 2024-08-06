package com.github.cyberedcake.hystats.command;

import com.github.cyberedcake.hystats.utils.UChat;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;
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
    public final @Nullable String prefix;
    public final String description;
    public final String[] aliases;

    List<IChatComponent> sentMessages = new ArrayList<>();

    public StatsCategoryCommand(String name, @Nullable String prefix, String description, String... aliases) {
        this.name = name;
        this.prefix = prefix;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(ICommandSender sender, GameStats stats, boolean oneLine, String[] args);

    protected void send(String msg) {
        send(msg, null);
    }

    protected void send(String msg, @Nullable String hover) {
        IChatComponent component = UChat.chat(msg);
        if (hover != null) {
            component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, UChat.chat(hover)));
        }
        sentMessages.add(component);
    }

    protected void send(IChatComponent msg) {
        sentMessages.add(msg);
    }

}
