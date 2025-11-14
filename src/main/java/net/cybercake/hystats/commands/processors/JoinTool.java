package net.cybercake.hystats.commands.processors;

import com.google.common.collect.ImmutableList;
import net.cybercake.hystats.utils.UChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JoinTool implements StreamOp {

    @Override
    public List<IChatComponent> apply(List<IChatComponent> input, String[] args) {
        IChatComponent component = new ChatComponentText("");
        for (int i = 0; i < input.size(); i++) {
            if (i != 0) {
                component.appendSibling(UChat.format(String.join(" ", args)));
            }
            component.appendSibling(input.get(i));
        }
        return ImmutableList.of(component);
    }

}
