package net.cybercake.hystats.commands.processors;

import net.minecraft.util.IChatComponent;

import java.util.List;

public interface StreamOp {

    List<IChatComponent> apply(List<IChatComponent> input, String[] args);

}
