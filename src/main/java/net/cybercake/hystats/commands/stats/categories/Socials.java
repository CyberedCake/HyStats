package net.cybercake.hystats.commands.stats.categories;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.cybercake.hystats.commands.stats.StatsCategoryCommand;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.hypixel.SocialMedia;
import net.cybercake.hystats.utils.ApiUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;

import java.util.Map;

import static net.cybercake.hystats.utils.UChat.format;

public class Socials extends StatsCategoryCommand {

    public Socials() {
        super("socials", "socialMedia", "Checks the social media accounts of a certain Hypixel player.", "sm", "socialmedia", "media");
    }


    @Override
    public void execute(ICommandSender sender, GameStats stats, boolean compact) {
        JsonObject object = stats.getObjectProperty("links");
        Map<String, String> map = new Gson().fromJson(object, new TypeToken<Map<String, String>>() {}.getType());
        if (object == null || map.isEmpty()) {
            text(stats.getUser() + " &chas no social media set!"); return;
        }

        text(stats.getUser() + "&f" + (stats.player().getName().endsWith("s") ? "'" : "'s") + " " + (compact ? "SM" : "Linked Social Media"));
        if (!compact) text(" ");

        for (Map.Entry<String, String> socialEntry : map.entrySet()) {
            String name = socialEntry.getKey();
            String link = socialEntry.getValue();
            SocialMedia social = SocialMedia.getSocial(name);
            boolean isLink = ApiUtils.isUrl(link);

            IChatComponent component;

            HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, format(
                    stats.getUser() + "&f'" + (stats.player().getName().endsWith("s") ? " " : "s ") + (social == null ? name : social.basic) + "&f: &b" + (isLink ? "&n" : "") + link + "\n\n" +
                            "&cThis account is not made or managed by Hypixel or Hystats!\n" +
                            "&cUse discretion when visiting external social media accounts." +
                            "\n\n" +
                            (isLink ? "&eClick here to open link!"
                                    : "&eClick here to paste in chat!")
            ));
            ClickEvent click = new ClickEvent(isLink ? ClickEvent.Action.OPEN_URL : ClickEvent.Action.SUGGEST_COMMAND, link);
            if (compact) {
                component = format((social == null ? "&7" + name : social.shortened));
                component.getChatStyle()
                        .setChatHoverEvent(hover)
                        .setChatClickEvent(click);
            } else {
                component = format((social == null ? "&7" + name : social.display) + "&f: &a" + link.replace("https://", ""));
                component.getChatStyle()
                        .setChatHoverEvent(hover)
                        .setChatClickEvent(click);
            }

            text(component);
        }
    }
}
