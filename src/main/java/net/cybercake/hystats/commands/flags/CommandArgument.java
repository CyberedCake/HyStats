package net.cybercake.hystats.commands.flags;

import net.cybercake.hystats.utils.VariableObject;

import javax.annotation.Nullable;

public class CommandArgument extends VariableObject {

    private final String title;
    private final @Nullable Object value;

    CommandArgument(String title, @Nullable Object value) {
        super(value);
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean hasValue() { return this.exists() && !this.get().getClass().equals(NoValue.class); }

    @Override
    public String toString() {
        return "CommandArgument{" +
                "title='" + title + '\'' +
                ", value=" + value +
                '}';
    }
}
