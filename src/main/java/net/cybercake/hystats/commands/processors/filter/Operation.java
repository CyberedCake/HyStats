package net.cybercake.hystats.commands.processors.filter;

public class Operation {

    public static Operation from(String string) {
        return new Operation(string.toCharArray());
    }

    private final char[] characters;

    private Operation(char[] characters) {
        this.characters = characters;
    }

    private boolean compare(Number one, Number two, char op) {
        double first = one.doubleValue();
        double second = two.doubleValue();
        switch (op) {
            case '=':
                return first == second;
            case '>':
                return first > second;
            case '<':
                return first < second;
        }
        return false;
    }

    public boolean compare(Number one, Number two) {
        for (char character : characters) {
            if (!compare(one, two, character)) {
                return false;
            }
        }
        return true;
    }

}
