package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.CollectionUtils;
import net.cybercake.hystats.utils.ColorCode;
import net.cybercake.hystats.utils.UChat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RedirectPipe implements StreamOp{
    @Override
    public Map<IChatComponent, GameStats> apply(Map<IChatComponent, GameStats> input, String[] args) {
        String uneditedCommand = String.join(" ", args).toLowerCase();
        if (!uneditedCommand.contains("$fmsg") && !uneditedCommand.contains("$msg")) {
            return CollectionUtils.singletonMap(UChat.format("&cExpected placeholder '$msg' or '$fmsg' in arguments, found none.", "&cArguments: &8" + uneditedCommand, false), null);
        }

        for (int i = 0; i < input.size(); i++) {
            IChatComponent component = new ArrayList<>(input.keySet()).get(i);

            String command = uneditedCommand
                    .replace("$fmsg", component.getUnformattedText())
                    .replace("$msg", ColorCode.stripColor(component.getUnformattedText()));
            System.out.println("Executing: " + command);

            int finalI = i;
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(finalI * 500L);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
                } catch (Exception exception) {
                    System.err.println("Error on " + RedirectPipe.class.getCanonicalName() + ": " + exception);
                }
            });
        }
        return new HashMap<>();
    }
}
