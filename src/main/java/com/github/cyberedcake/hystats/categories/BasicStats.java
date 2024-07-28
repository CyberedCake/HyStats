package com.github.cyberedcake.hystats.categories;

import com.github.cyberedcake.hystats.command.StatsCategoryCommand;
import net.hypixel.api.reply.PlayerReply;
import net.minecraft.command.ICommandSender;

public class BasicStats extends StatsCategoryCommand {

    public BasicStats() {
        super("general", "general", "View the basic stats from Hypixel for the player.", "basic");
    }


    @Override
    public void execute(ICommandSender sender, PlayerReply.Player player, String[] args) {
        send("Basic Stats of " + player.getHighestRank() + " " + player.getName());

    }
}
