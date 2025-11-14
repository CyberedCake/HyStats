package net.cybercake.hystats.commands.processors;

import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GrepTool implements StreamOp {

    @Override
    public List<IChatComponent> apply(List<IChatComponent> input, String[] args) {
        Pattern pattern = Pattern.compile(String.join(" ", args));
        List<IChatComponent> messages = input.stream().filter(s -> pattern.matcher(s.getFormattedText()).find()).collect(Collectors.toList());
        return new HighlightTool().apply(messages, args);
    }

}
