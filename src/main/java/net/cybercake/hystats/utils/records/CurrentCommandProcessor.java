package net.cybercake.hystats.utils.records;

import net.cybercake.hystats.commands.processors.StreamOp;

import java.util.List;

public class CurrentCommandProcessor {

    public static CurrentCommandProcessor of(StreamOp op, List<String> args) {
        return new CurrentCommandProcessor(op, args);
    }

    private final StreamOp op;
    private final List<String> args;

    private CurrentCommandProcessor(StreamOp op, List<String> args) {
        this.op = op;
        this.args = args;
    }

    public StreamOp op() { return this.op; }
    public List<String> args() { return this.args; }

}
