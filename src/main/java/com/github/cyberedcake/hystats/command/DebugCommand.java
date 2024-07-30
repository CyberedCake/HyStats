package com.github.cyberedcake.hystats.command;

import com.github.cyberedcake.hystats.ExampleMod;
import com.github.cyberedcake.hystats.utils.UChat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DebugCommand extends CommandBase {

    static List<UUID> allowedUuids = new ArrayList<>();
    static {
        allowedUuids.add(UUID.fromString("98c87f21-bca9-4726-8b9e-e5cd2ef685f4")); // CyberedCake
        allowedUuids.add(UUID.fromString("64064df5-cb48-4fec-bb6f-b7aa7c33d94a")); // CyberedCake2
    }

    @Override
    public String getCommandName() {
        return "$statsdebug";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/$statsdebug";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args[0].equalsIgnoreCase("--reload-api")) {
            if (ExampleMod.API != null) {
                UChat.send("&7&oShutting down API...", null, false);
                ExampleMod.shutdownApi();
            }

            ExampleMod.loadApi();
            UChat.send("&aReloaded the Hypixel API!", null, false);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (!(sender instanceof EntityPlayer)) return true;
        return allowedUuids.contains(((EntityPlayer) sender).getUniqueID());
    }

}
