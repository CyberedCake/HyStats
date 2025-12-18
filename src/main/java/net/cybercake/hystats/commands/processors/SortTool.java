package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.utils.records.ProcessedStatsOutput;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortTool implements StreamOp{

    public static class StatsOutputComparator implements Comparator<ProcessedStatsOutput> {

        private final String statName;

        public StatsOutputComparator(String statName) {
            this.statName = statName;
        }

        @Override
        public int compare(ProcessedStatsOutput o1, ProcessedStatsOutput o2) {
            return (int) ((o1.stats().findStat(statName).getAsDouble() * 1000L) - (o2.stats().findStat(statName).getAsDouble() * 1000L));
        }
    }

    @Override
    public List<ProcessedStatsOutput> apply(List<ProcessedStatsOutput> input, String[] args) {
        if (args.length < 1) {
            return Collections.emptyList();
        }
        input.sort(new StatsOutputComparator(args[0]));

        if (args.length > 1 && args[1].startsWith("d")) {
            Collections.reverse(input);
        }

        return input;
    }

}
