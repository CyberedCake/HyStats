package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.commands.processors.filter.StatFilterTool;
import net.cybercake.hystats.utils.records.CurrentCommandProcessor;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.CollectionUtils;
import net.cybercake.hystats.utils.Pair;
import net.cybercake.hystats.utils.UChat;
import net.cybercake.hystats.utils.records.ProcessedStatsOutput;
import net.minecraft.util.IChatComponent;

import java.util.*;

public class Processors {

    private static class ProcessorBuilder
    {
        StreamOp processor = null;
        List<String> args = new ArrayList<>();
    }

    private final String[] args;
    private final List<CurrentCommandProcessor> thisCommandProcessors;
    private int startIndex;

    public Processors(String[] args) {
        this.args = args;
        this.thisCommandProcessors = new ArrayList<>();
        this.startIndex = -1;

        final StreamOp DEFAULT_PROCESSOR = new RedirectPipe();
        Map<String, StreamOp> processors = new HashMap<>();
        processors.put("grep", new GrepTool());
        processors.put("highlight", new HighlightTool());
        processors.put("join", new JoinTool());
        processors.put("redirect", new RedirectPipe());
        processors.put("filter", new StatFilterTool());
        processors.put("sort", new SortTool());

        ProcessorBuilder builder = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.contains("|") || arg.contains("@")) {
                if (startIndex == -1) startIndex = i;

                this.finalizeBuilder(builder);
                builder = new ProcessorBuilder();

                arg = arg.replace("|", "").replace("@", "");
                if (arg.isEmpty())
                    continue;
            }

            if (builder == null)
                continue;

            if (builder.processor != null) {
                builder.args.add(arg);
                continue;
            }

            if (processors.containsKey(arg)) {
                builder.processor = processors.get(arg);
            } else {
                builder.processor = DEFAULT_PROCESSOR;
                builder.args.add(arg);
            }
        }
        this.finalizeBuilder(builder);
    }

    private void finalizeBuilder(ProcessorBuilder builder) {
        if (builder == null) {
            return;
        }

        this.thisCommandProcessors.add(CurrentCommandProcessor.of(builder.processor, builder.args));
    }

    public String[] removeData() {
        if (this.startIndex == -1) {
            return args;
        }
        return Arrays.copyOfRange(args, 0, this.startIndex);
    }

    public List<ProcessedStatsOutput> streamList(List<ProcessedStatsOutput> components) {
        List<ProcessedStatsOutput> result = new ArrayList<>();
        for (ProcessedStatsOutput component : components) {
            if (component.stats().isEmpty())
                continue;
            result.add(component);
        }

        System.out.println("Streaming list of chat components via " + Processors.class.getCanonicalName() + "!");
        for (CurrentCommandProcessor curr : this.thisCommandProcessors) {
            System.out.println("| curr: " + curr);
            String[] args = curr.args().toArray(new String[0]);
            System.out.println("| with arguments: " + Arrays.toString(args));
            result = curr.op().apply(result, args);
        }
        System.out.println("Complete streaming.");

        if (result .isEmpty()) {
            result .add(ProcessedStatsOutput.of(UChat.format("&cNo returned results.", "&cProcessors: &8" + this, false), null));
        }

        return result;
    }

    public int countActiveProcessors() { return this.thisCommandProcessors.size(); }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "Processors[", "]");
        for (CurrentCommandProcessor curr : this.thisCommandProcessors) {
            joiner.add("{" + curr.op().getClass().getCanonicalName() + ", args: " + curr.args().toString() + "}");
        }
        return joiner.toString();
    }
}
