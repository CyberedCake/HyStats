package net.cybercake.hystats.commands.flags;

import net.cybercake.hystats.utils.UChat;

import javax.annotation.Nullable;
import java.util.*;

public class Arguments {

    public static Arguments empty() {
        return new Arguments(new String[]{});
    }

    private final Map<String, CommandArgument> arguments;

    public Arguments(String[] args) {
        this.arguments = new HashMap<>();

        for (String arg : args) {

            if (!arg.startsWith("-")) continue;

            arg = arg.replace("-", "");

            if (!arg.contains("=")) {
                this.arguments.put(arg, new CommandArgument(arg, new NoValue()));
                continue;
            }

            String[] tokens = arg.split("=");
            this.arguments.put(tokens[0], new CommandArgument(tokens[0], tokens[1]));
        }
    }

    public CommandArgument arg(String name, String... aliases) {
        String[] titles = Arrays.copyOf(aliases, aliases.length + 1);
        titles[aliases.length] = name;

        for (String title : titles) {
            CommandArgument argument = arguments.get(title);
            if (argument == null) {
                continue;
            }
            System.out.println("Found: " + argument);
            return argument;
        }
        System.out.println("Not found argument");
        return new CommandArgument(name, null);
    }

    @Override
    public String toString() {
        return "Arguments[" + arguments.toString() + "]";
    }
}
