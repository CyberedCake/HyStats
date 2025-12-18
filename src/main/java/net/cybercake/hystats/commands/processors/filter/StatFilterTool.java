package net.cybercake.hystats.commands.processors.filter;

import net.cybercake.hystats.commands.processors.StreamOp;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.Pair;
import net.cybercake.hystats.utils.records.ProcessedStatsOutput;
import net.minecraft.util.IChatComponent;

import java.util.*;

public class StatFilterTool implements StreamOp {
    @Override
    public List<ProcessedStatsOutput> apply(List<ProcessedStatsOutput> input, String[] args) {
        System.out.println("Args: " + Arrays.toString(args));

        String first = args[0];
        Number firstNumber = asNumber(first);

        Operation operation = Operation.from(args[1]);

        String second = args[2];
        Number secondNumber = asNumber(second);

        System.out.println("firstNumber=" + firstNumber + ", op=" + operation + ", secondNumber=" + secondNumber);

        List<ProcessedStatsOutput> output = new ArrayList<>();
        for (ProcessedStatsOutput entry : input) {
            GameStats stats = entry.stats();
            Number specificFirstNumber = firstNumber;
            if (firstNumber == null) {
                specificFirstNumber = asNumber(stats.findStat(first).getAsString());
                System.out.println("as of entry " + stats.getUsername() + ": specific firstNumber=" + specificFirstNumber);
            }
            Number specificSecondNumber = secondNumber;
            if (secondNumber == null) {
                specificSecondNumber = asNumber(stats.findStat(second).getAsString());
                System.out.println("as of entry " + stats.getUsername() + ": specific secondNumber=" + specificSecondNumber);
            }


            System.out.println("op: " + specificFirstNumber + " " + operation.toString() + " " + specificSecondNumber);
            boolean cmp = operation.compare(specificFirstNumber, specificSecondNumber);
            System.out.println("|- cmp ret: " + cmp);
            if (cmp) {
                output.add(ProcessedStatsOutput.of(entry.chat(), stats));
            }
        }

        return output;
    }

    private Number asNumber(String str) {
        if (isInteger(str)) {
            return Integer.parseInt(str);
        } else if (isDouble(str)) {
            return Double.parseDouble(str);
        }
        return null;
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
