package net.cybercake.hystats.events;

import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.commands.stats.StatsCategoryCommand;
import net.cybercake.hystats.commands.stats.StatsCommandManager;
import net.cybercake.hystats.hypixel.exceptions.HyStatsError;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CheckPartyList {

    private final ICommandSender sender;
    private final StatsCategoryCommand statsType;

    private Status status;
    private List<String> potentialUsernames;
    private int lines;

    private ListenForChat listener;

    public CheckPartyList(ICommandSender sender, StatsCategoryCommand statsType) {
        if (!HyStats.getConnectedServer().toString().contains("hypixel.net")) {
            throw new HyStatsError(7, "You must be on Hypixel to use this command!");
        }

        this.sender = sender;
        this.statsType = statsType;

        this.status = Status.WAITING;
        this.potentialUsernames = new ArrayList<>();
        this.lines = 0;

        this.listener = new ListenForChat();
        MinecraftForge.EVENT_BUS.register(this.listener);

        Minecraft.getMinecraft().thePlayer.sendChatMessage("/party list");
    }

    public void done() {
        MinecraftForge.EVENT_BUS.unregister(this.listener);
    }

    public class ListenForChat {

        @SubscribeEvent
        public void chatSent(ClientChatReceivedEvent event) {
            try {
                String message = event.message.getUnformattedText();
                if (message.contains(new String(new char[53]).replace("\0", "-"))) {
                    event.setCanceled(true);
                    status = status == Status.WAITING ? Status.WAITING_FOR_MEMBERS : Status.EXITED;
                }

                if (status == Status.WAITING_FOR_MEMBERS) {
                    if (!message.contains("Party Members (")) {
                        return;
                    }
                    event.setCanceled(true);
                    status = Status.IN_MESSAGE;
                }

                if (lines >= 10 || status == Status.EXITED) {
                    CheckPartyList.this.done();
                    if (CheckPartyList.this.potentialUsernames.isEmpty()) {
                        HyStats.command.findAllInParty(sender, statsType, potentialUsernames,
                                new HyStatsError(8, "No players found in your party.")
                                );
                        return;
                    }
                    CompletableFuture.runAsync(() -> {
                        HyStats.command.findAllInParty(sender, statsType, potentialUsernames, null);
                    });
                    return;
                }

                if (status == Status.WAITING) {
                    return;
                }

                event.setCanceled(true);

                lines++;

                message =
                        message.replace("Party Leader: ", "")
                                .replace("Party Moderators: ", "")
                                .replace("Party Members: ", "")
                                .replace("Party Members", "")
                ;
                if (message.isEmpty()) return;

                String[] tokens = message.split(" ");
                for (String token : tokens) {
                    if (!StringUtils.isAlphanumeric(token.replace("_", ""))) continue;

                    CheckPartyList.this.potentialUsernames.add(token);
                }
            } catch (Exception exception) {
                CheckPartyList.this.done();
                HyStats.command.findAllInParty(sender, statsType, new ArrayList<>(), exception);
            }
        }

    }

    public enum Status {
        WAITING, WAITING_FOR_MEMBERS, IN_MESSAGE, EXITED
    }

}
