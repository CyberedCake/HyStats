package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.utils.UChat;
import net.minecraft.util.IChatComponent;

import java.util.*;

public class Processors {

    private static class ProcessorBuilder
    {
        StreamOp processor = null;
        List<String> args = new ArrayList<>();
    }

    private String[] args;
    private final Map<StreamOp, List<String>> thisCommandProcessors;
    private int startIndex;

    public Processors(String[] args) {
        this.args = args;
        this.thisCommandProcessors = new HashMap<>();
        this.startIndex = -1;

        final StreamOp DEFAULT_PROCESSOR = new RedirectPipe();
        Map<String, StreamOp> processors = new HashMap<>();
        processors.put("grep", new GrepTool());
        processors.put("highlight", new HighlightTool());
        processors.put("join", new JoinTool());
        processors.put("redirect", new RedirectPipe());

        ProcessorBuilder builder = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.contains("|") || arg.contains(">")) {
                if (startIndex == -1) startIndex = i;

                this.finalizeBuilder(builder);
                builder = new ProcessorBuilder();

                arg = arg.replace("|", "").replace(">", "");
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

        this.thisCommandProcessors.put(builder.processor, builder.args);
    }

    public String[] removeData() {
        if (this.startIndex == -1) {
            return args;
        }
        return Arrays.copyOfRange(args, 0, this.startIndex);
    }

    public List<IChatComponent> streamList(List<IChatComponent> components) {
        System.out.println("Streaming list of chat components via " + Processors.class.getCanonicalName() + "!");
        for (Map.Entry<StreamOp, List<String>> op : this.thisCommandProcessors.entrySet()) {
            String[] args = op.getValue().toArray(new String[0]);

            components = op.getKey().apply(components, args);
        }

        if (components.isEmpty()) {
            components.add(UChat.format("&cNo results."));
        }

        return components;
    }

}
