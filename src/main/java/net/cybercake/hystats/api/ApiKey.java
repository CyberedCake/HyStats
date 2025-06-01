package net.cybercake.hystats.api;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class ApiKey {

    private final String key;
    private final boolean custom;

    ApiKey(String property, String env) {
        this(System.getProperty(property, System.getenv(env)), false);
    }

    ApiKey(String key, boolean custom) {
        Preconditions.checkNotNull(key, "No API key was passed in");

        this.key = key;
        this.custom = custom;

        // hide API key if it ever shows up in logs for whatever reason
        // (sometimes an exception can do this from below)
        System.setErr(newPrintStream(this.key, System.err));
        System.setOut(newPrintStream(this.key, System.out));
    }

    public String getApiKey() {
        return this.key;
    }

    public boolean isCustom() {
        return this.custom;
    }

    private PrintStream newPrintStream(String censor, PrintStream old) {
        return new PrintStream(old) {
            @Override
            public void println(@Nullable String x) {
                old.println(x != null ? x.replace(censor, "***") : null);
            }

            @Override
            public void print(@Nullable String s) {
                old.print(s != null ? s.replace(censor, "***") : null);
            }
        };
    }

}
