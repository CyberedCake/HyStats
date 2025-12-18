package net.cybercake.hystats.commands.processors;

import com.google.common.collect.ImmutableList;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.Pair;
import net.cybercake.hystats.utils.UChat;
import net.cybercake.hystats.utils.records.ProcessedStatsOutput;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.*;
import java.util.stream.Collectors;

public class JoinTool implements StreamOp {

    @Override
    public List<ProcessedStatsOutput> apply(List<ProcessedStatsOutput> input, String[] args) {
        IChatComponent component = new ChatComponentText("");
        for (int i = 0; i < input.size(); i++) {
            if (i != 0) {
                component.appendSibling(UChat.format(String.join(" ", args)));
            }
            component.appendSibling(new ArrayList<>(input.stream().map(ProcessedStatsOutput::chat).collect(Collectors.toList())).get(i));
        }
        List<ProcessedStatsOutput> stats = new ArrayList<>();
        stats.add(ProcessedStatsOutput.of(component, null));
        return stats;
    }

}
