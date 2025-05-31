package net.cybercake.hystats.utils;

import java.util.ArrayList;
import java.util.List;

public class UTabCompletions {

    public static List<String> tab(String currentArg, List<String> completions) {
        if (currentArg == null || currentArg.isEmpty()) {
            return completions;
        }

        currentArg = currentArg.toLowerCase();

        List<String> returnedCompletions = new ArrayList<>();
        for (String str : completions) {
            if (str.toLowerCase().startsWith(currentArg)) {
                returnedCompletions.add(str);
            }
        }

        return returnedCompletions;
    }

}
