package net.cybercake.hystats.commands.flags;

import scala.reflect.internal.Trees;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("DataFlowIssue")
public class CommandArgument {

    private final String title;
    private final @Nullable Object value;

    CommandArgument(String title, @Nullable Object value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean exists() {
        return this.get() != null;
    }
    public boolean hasValue() { return this.exists() && !this.get().getClass().equals(NoValue.class); }

    public Object get() { return this.value; }
    public String getAsString() { return get(() -> this.value.toString(), null); }
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


    @Override
    public String toString() {
        return "CommandArgument{" +
                "title='" + title + '\'' +
                ", value=" + value +
                '}';
    }
}
