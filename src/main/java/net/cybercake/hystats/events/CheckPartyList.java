package net.cybercake.hystats.events;

import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.commands.stats.RequestProcessor;
import net.cybercake.hystats.exceptions.HyStatsError;
import net.cybercake.hystats.utils.UChat;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CheckPartyList {

    private final RequestProcessor processor;

    private Status status;
    private final List<String> potentialUsernames;
    private int lines;

    private final ListenForChat listener;

    public CheckPartyList(RequestProcessor processor) {
        if (!HyStats.isHypixel()) {
            throw new HyStatsError(7, "You must be on Hypixel to use this command!");
        }

        this.processor = processor;

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
                if (message.contains(UChat.repeat("-", 53))) {
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
                        processor.mass().showAsParty(new ArrayList<>(), new HyStatsError(8, "No players found in your party."));
                        return;
                    }
                    CompletableFuture.runAsync(() -> {
                        processor.mass().showAsParty(potentialUsernames, null);
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
                processor.mass().showAsParty(new ArrayList<>(), exception);
            }
        }

    }

    public enum Status {
        WAITING, WAITING_FOR_MEMBERS, IN_MESSAGE, EXITED
    }

}
