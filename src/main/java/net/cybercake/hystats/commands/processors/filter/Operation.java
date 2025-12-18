package net.cybercake.hystats.commands.processors.filter;

import java.util.Arrays;

public class Operation {

    public static Operation from(String string) {
        return new Operation(string.toCharArray());
    }

    private final char[] characters;
    private boolean inversion;

    private Operation(char[] characters) {
        this.characters = characters;
    }

    private boolean compare(Number one, Number two, char op) {
        double first = one.doubleValue();
        double second = two.doubleValue();
        boolean result = false;
        switch (op) {
            case '=':
                result = first == second; break;
            case '>':
                result = first > second; break;
            case '<':
                result = first < second; break;
        }

        return result;
    }

    public boolean compare(Number one, Number two) {
        for (char character : characters) {
            if (character == '!' || character == '~') {
                inversion = !inversion;
                continue;
            }

            boolean result = compare(one, two, character);
            if (inversion) {
                result = !result;
                inversion = false;
            }
            return result;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Operation[characters=" + Arrays.toString(characters) + "]";
    }
}
