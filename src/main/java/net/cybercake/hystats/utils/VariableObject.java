package net.cybercake.hystats.utils;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

@SuppressWarnings("DataFlowIssue")
public abstract class VariableObject {

    private final @Nullable Object obj;

    public VariableObject(@Nullable Object obj) {
        this.obj = obj;
    }

    public boolean exists() {
        return this.get() != null;
    }

    public Object get() { return this.obj; }
    public String getAsString() { return get(() -> this.obj.toString(), null); }
    public byte getAsByte() { return get(() -> Byte.parseByte(getAsString()), Byte.MIN_VALUE); }
    public short getAsShort() { return get(() -> Short.parseShort(getAsString()), Short.MIN_VALUE); }
    public int getAsInt() { return get(() -> Integer.parseInt(getAsString()), Integer.MIN_VALUE); }
    public long getAsLong() { return get(() -> Long.parseLong(getAsString()), Long.MIN_VALUE); }
    public float getAsFloat() { return get(() -> Float.parseFloat(getAsString()), Float.MIN_VALUE); }
    public double getAsDouble() { return get(() -> Double.parseDouble(getAsString()), Double.MIN_VALUE); }
    public boolean getAsBoolean() { return get(() -> Boolean.parseBoolean(getAsString()), false); }


    private <T> T get(Callable<T> supply, T def) {
        try {
            return supply.call();
        } catch (Throwable throwable) {
            throwable.printStackTrace(System.err);
            System.out.println("Executable threw an exception: " + throwable);
            return def;
        }
    }

}
