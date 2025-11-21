package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.hypixel.GameStats;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.Map;

public interface StreamOp {

    Map<IChatComponent, GameStats> apply(Map<IChatComponent, GameStats> input, String[] args);

}
