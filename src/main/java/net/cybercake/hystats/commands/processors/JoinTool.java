package net.cybercake.hystats.commands.processors;

import com.google.common.collect.ImmutableList;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.UChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.*;

public class JoinTool implements StreamOp {

    @Override
    public Map<IChatComponent, GameStats> apply(Map<IChatComponent, GameStats> input, String[] args) {
        IChatComponent component = new ChatComponentText("");
        for (int i = 0; i < input.size(); i++) {
            if (i != 0) {
                component.appendSibling(UChat.format(String.join(" ", args)));
            }
            component.appendSibling(new ArrayList<>(input.keySet()).get(i));
        }
        Map<IChatComponent, GameStats> stats = new HashMap<>();
        stats.put(component, null);
        return stats;
    }

}
