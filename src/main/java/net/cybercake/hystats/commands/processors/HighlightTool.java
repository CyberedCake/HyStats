package net.cybercake.hystats.commands.processors;

import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.ColorCode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HighlightTool implements StreamOp{
    @Override
    public Map<IChatComponent, GameStats> apply(Map<IChatComponent, GameStats> input, String[] args) {
        Pattern pattern = Pattern.compile(String.join(" ", args));

        Map<IChatComponent, GameStats> output = new HashMap<>();
        for (Map.Entry<IChatComponent, GameStats> entry : input.entrySet()) {
            output.put(highlight(entry.getKey(), pattern), entry.getValue());
        }
        return output;
    }

    private IChatComponent highlight(IChatComponent original, Pattern pattern) {
        String text = ColorCode.stripColor(original.getUnformattedText());
        Matcher matcher = pattern.matcher(text);
        System.out.println("Streaming: " + text );

        if (!matcher.find()) {
            return original;
        }
        System.out.println("** MATCH IN THIS ONE **");

        IChatComponent replacement = new ChatComponentText("");
        replacement.setChatStyle(original.getChatStyle().createDeepCopy());

        if (!original.getSiblings().isEmpty()) {
            for (IChatComponent sibling : original.getSiblings()) {
                System.out.println("- Found sibling: " + sibling.getUnformattedText());
                replacement.appendSibling(highlight(sibling, pattern));
            }
            return replacement;
        }

        int iterations = 0;
        int last = 0;
        ChatStyle newStyle = original.getChatStyle().createDeepCopy();

        do {
            if (iterations > 5000) {
                System.out.println("** ITERATIONS REACHED MAXIMUM, EXITING LOOP... **");
                System.out.println("** ITERATIONS REACHED MAXIMUM, EXITING LOOP... **");
                System.out.println("** ITERATIONS REACHED MAXIMUM, EXITING LOOP... **");
                break;
            }
            iterations++;

            int start = matcher.start();
            int end = matcher.end();
            System.out.println("found match: " + start + " to " + end);
            System.out.println("\t(which is " + text.substring(start, end) + " out of '" + text + "')");

                // previous
            IChatComponent previous = new ChatComponentText("");
            if (start > last) {
                previous = this.createComponent(original, last, start);
                newStyle = previous.getChatStyle().createDeepCopy();
            }
            replacement.appendSibling(previous);

            // highlight match
            String match = text.substring(start, end);
            IChatComponent highlight = new ChatComponentText(match);
            highlight.setChatStyle(extractStyleFromFormattedText(original.getUnformattedText(), original.getChatStyle(), start));
            highlight.getChatStyle().setColor(EnumChatFormatting.GOLD);
            highlight.getChatStyle().setUnderlined(true);
            previous.appendSibling(highlight);

            last = end;
        } while (matcher.find());

        if (last < text.length()) {
            IChatComponent component = new ChatComponentText(text.substring(last));
            component.setChatStyle(newStyle);
            replacement.getSiblings().add(component);
        }

        System.out.println("Original: " + text + " w/ " + original.getSiblings().size() + " siblings");
        System.out.println("Replacement: " + replacement.getUnformattedText());

        return replacement;
    }

    private IChatComponent createComponent(IChatComponent original, int startIndex, int endIndex) {
        IChatComponent component = new ChatComponentText(ColorCode.stripColor(original.getUnformattedText()).substring(startIndex, endIndex));
        component.setChatStyle(extractStyleFromFormattedText(original.getUnformattedText(), original.getChatStyle(), endIndex));
        return component;
    }

    private ChatStyle extractStyleFromFormattedText(String formatted, ChatStyle originalStyle, int endIndex) {
        ChatStyle style = originalStyle.createDeepCopy();

        for (int i = 0; i < endIndex - 1; i++) {
            if (formatted.charAt(i) == ColorCode.getCharacter()) { // § sign
                char code = Character.toLowerCase(formatted.charAt(i + 1));
                ColorCode color = ColorCode.getColorFromCode(code);

                if (color != null) {
                    if (color.isColor()) {
                        style.setColor(color.parseAsChatFormatting());
                    } else if (color == ColorCode.BOLD) {
                        style.setBold(true);
                    } else if (color == ColorCode.UNDERLINE) {
                        style.setUnderlined(true);
                    } else if (color == ColorCode.ITALIC) {
                        style.setItalic(true);
                    } else if (color == ColorCode.STRIKETHROUGH) {
                        style.setStrikethrough(true);
                    } else if (color == ColorCode.RESET) {
                        style = new ChatStyle(); // reset style
                    }
                }
            }
        }

        return style;
    }
}
