package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.Pair;
import net.cybercake.hystats.utils.records.ProcessedStatsOutput;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GrepTool implements StreamOp {

    @Override
    public List<ProcessedStatsOutput> apply(List<ProcessedStatsOutput> input, String[] args) {
        Pattern pattern = Pattern.compile(String.join(" ", args));

        List<ProcessedStatsOutput> output = new ArrayList<>();
        for (ProcessedStatsOutput entry : input) {
            if (!pattern.matcher(entry.chat().getFormattedText()).find()) {
                continue;
            }
            output.add(entry);
        }

        return new HighlightTool().apply(output, args);
    }

}
