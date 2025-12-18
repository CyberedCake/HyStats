package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.Pair;
import net.cybercake.hystats.utils.records.ProcessedStatsOutput;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.Map;

public interface StreamOp {

    List<ProcessedStatsOutput> apply(List<ProcessedStatsOutput> input, String[] args);

}
