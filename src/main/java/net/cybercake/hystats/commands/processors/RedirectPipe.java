package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.CollectionUtils;
import net.cybercake.hystats.utils.ColorCode;
import net.cybercake.hystats.utils.Pair;
import net.cybercake.hystats.utils.UChat;
import net.cybercake.hystats.utils.records.ProcessedStatsOutput;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedirectPipe implements StreamOp{
    @Override
    public List<ProcessedStatsOutput> apply(List<ProcessedStatsOutput> input, String[] args) {
        String uneditedCommand = String.join(" ", args).toLowerCase();
        if (!uneditedCommand.contains("$fmsg") && !uneditedCommand.contains("$msg")) {
            return CollectionUtils.singleItemList(
                    ProcessedStatsOutput.of(UChat.format("&cExpected placeholder '$msg' or '$fmsg' in arguments, found none.", "&cArguments: &8" + uneditedCommand, false), null)
            );
        }

        int delay;
        TimeUnit unit;
        if (uneditedCommand.contains(">") && uneditedCommand.split(">").length == 2) {
            String[] splitDelay = uneditedCommand.split(">");
            uneditedCommand = splitDelay[1];
            String rawDelay = splitDelay[0].replaceAll("\\s+", ""); // remove whitespace (equiv .strip())

            if (rawDelay.contains("ms"))        unit = TimeUnit.MILLISECONDS;
            else if (rawDelay.contains("m"))    unit = TimeUnit.MINUTES;
            else if (rawDelay.contains("h"))    unit = TimeUnit.HOURS;
            else                                unit = TimeUnit.SECONDS;

            rawDelay = rawDelay.replaceAll("[A-Za-z]*", "");
            delay = Integer.parseInt(rawDelay);
        } else {
            delay = 550;
            unit = TimeUnit.MILLISECONDS;
        }

        for (int i = 0; i < input.size(); i++) {
            IChatComponent component = new ArrayList<>(input.stream().map(ProcessedStatsOutput::chat).collect(Collectors.toList())).get(i);

            String command = uneditedCommand
                    .replace("$fmsg", component.getUnformattedText())
                    .replace("$msg", ColorCode.stripColor(component.getUnformattedText()));
            System.out.println("Executing: " + command);

            int finalI = i;
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(finalI * (unit.toMillis(delay)));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
                } catch (Exception exception) {
                    System.err.println("Error on " + RedirectPipe.class.getCanonicalName() + ": " + exception);
                }
            });
        }
        return Collections.singletonList(ProcessedStatsOutput.of(GameStats.empty(),
                UChat.format("&eRedirected &b" + input.size() + " &estats lines to &a" + uneditedCommand + "&e" + (delay == 0 ? "" : " with delay &d" + delay + " " + unit.name() + " &ebetween messages") + "!")
                ));
    }
}
