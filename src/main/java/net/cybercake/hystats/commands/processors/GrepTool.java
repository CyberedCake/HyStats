package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.hypixel.GameStats;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GrepTool implements StreamOp {

    @Override
    public Map<IChatComponent, GameStats> apply(Map<IChatComponent, GameStats> input, String[] args) {
        Pattern pattern = Pattern.compile(String.join(" ", args));

        Map<IChatComponent, GameStats> output = new HashMap<>();
        for (Map.Entry<IChatComponent, GameStats> entry : input.entrySet()) {
            if (!pattern.matcher(entry.getKey().getFormattedText()).find()) {
                continue;
            }
            output.put(entry.getKey(), entry.getValue());
        }

        return new HighlightTool().apply(output, args);
    }

}
