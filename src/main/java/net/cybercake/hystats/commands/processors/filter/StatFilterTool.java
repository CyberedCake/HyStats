package net.cybercake.hystats.commands.processors.filter;

import net.cybercake.hystats.commands.processors.StreamOp;
import net.cybercake.hystats.hypixel.GameStats;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.Map;

public class StatFilterTool implements StreamOp {
    @Override
    public Map<IChatComponent, GameStats> apply(Map<IChatComponent, GameStats> input, String[] args) {
        return Collections.emptyMap();
    }
}
