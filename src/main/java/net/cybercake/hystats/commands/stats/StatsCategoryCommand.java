package net.cybercake.hystats.commands.stats;

import net.cybercake.hystats.commands.flags.Arguments;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.UChat;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class StatsCategoryCommand {

    public final String name;
    public final @Nullable String prefix;
    public final String description;
    public final String[] aliases;

    final List<IChatComponent> messages = new ArrayList<>();

    public StatsCategoryCommand(String name, @Nullable String prefix, String description, String... aliases) {
        this.name = name;
        this.prefix = prefix;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(ICommandSender sender, GameStats stats, Arguments args, boolean compact);


    public void text(IChatComponent message) {
        this.messages.add(message);
    }

    public void text(String message, @Nullable String hover, @Nullable String clickCommand) {
        IChatComponent component = UChat.format(message, hover, false);
        if (clickCommand != null) {
            component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
        }
        this.text(component);
    }

    public void text(String message, @Nullable String hover) {
        this.text(UChat.format(message, hover, false));
    }

    public void text(String message) {
        this.text(message, null);
    }

}
