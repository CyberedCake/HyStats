package net.cybercake.hystats.exceptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExceptionManager {

    private final Map<Integer, StackTraceElement[]> exceptions;

    public ExceptionManager() {
        this.exceptions = new HashMap<>();
    }

    int put(StackTraceElement... stacktrace) {
        int code = Arrays.hashCode(stacktrace);
        this.exceptions.putIfAbsent(code, stacktrace);
        return code;
    }

    public StackTraceElement[] get(int integer) {
        return this.exceptions.getOrDefault(integer, null);
    }

}
