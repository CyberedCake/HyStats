package net.cybercake.hystats.commands.flags;

import javax.annotation.Nullable;
import java.util.*;

public class Arguments {

    private final Map<String, CommandArgument> arguments;

    public Arguments(String[] args) {
        this.arguments = new HashMap<>();

        for (String arg : args) {
            if (!arg.startsWith("-")) continue;

            arg = arg.replace("-", "");

            if (!arg.contains("=")) {
                this.arguments.put(arg, new CommandArgument(arg, null));
                continue;
            }

            String[] tokens = arg.split("=");
            this.arguments.put(tokens[0], new CommandArgument(tokens[0], tokens[1]));
        }
    }

    public @Nullable CommandArgument arg(String name, String... aliases) {
        String[] titles = Arrays.copyOf(aliases, aliases.length + 1);
        titles[aliases.length] = name;

        for (String title : titles) {
            CommandArgument argument = arguments.get(title);
            if (argument == null) {
                continue;
            }
            return argument;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Arguments[" + arguments.toString() + "]";
    }
}
