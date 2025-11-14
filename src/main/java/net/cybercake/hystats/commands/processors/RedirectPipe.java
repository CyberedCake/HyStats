package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.utils.ColorCode;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RedirectPipe implements StreamOp{
    @Override
    public List<IChatComponent> apply(List<IChatComponent> input, String[] args) {
        for (int i = 0; i < input.size(); i++) {
            IChatComponent component = input.get(i);
            String command = String.join(" ", args).toLowerCase()
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
        return Collections.emptyList();
    }
}
