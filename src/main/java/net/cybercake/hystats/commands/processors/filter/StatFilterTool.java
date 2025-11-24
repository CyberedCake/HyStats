package net.cybercake.hystats.commands.processors.filter;

import net.cybercake.hystats.commands.processors.StreamOp;
import net.cybercake.hystats.hypixel.GameStats;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StatFilterTool implements StreamOp {
    @Override
    public Map<IChatComponent, GameStats> apply(Map<IChatComponent, GameStats> input, String[] args) {
        System.out.println("Args: " + Arrays.toString(args));

        String first = args[0];
        Number firstNumber = asNumber(first);

        Operation operation = Operation.from(args[1]);

        String second = args[2];
        Number secondNumber = asNumber(second);

        System.out.println("firstNumber=" + firstNumber + ", op=" + operation + ", secondNumber=" + secondNumber);

        Map<IChatComponent, GameStats> output = new HashMap<>();
        for (Map.Entry<IChatComponent, GameStats> entry : input.entrySet()) {
            Number specificFirstNumber = firstNumber;
            if (firstNumber == null) {
                specificFirstNumber = asNumber(entry.getValue().findStat(first).getAsString());
                System.out.println("as of entry " + entry.getValue().getUsername() + ": specific firstNumber=" + specificFirstNumber);
            }
            Number specificSecondNumber = secondNumber;
            if (secondNumber == null) {
                specificSecondNumber = asNumber(entry.getValue().findStat(second).getAsString());
                System.out.println("as of entry " + entry.getValue().getUsername() + ": specific secondNumber=" + specificSecondNumber);
            }


            if (operation.compare(specificFirstNumber, specificSecondNumber)) {
                output.put(entry.getKey(), entry.getValue());
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
