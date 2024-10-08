package com.github.cyberedcake.hystats.categories;

import com.github.cyberedcake.hystats.command.GameStats;
import com.github.cyberedcake.hystats.command.StatsCategoryCommand;
import com.github.cyberedcake.hystats.hypixel.SocialMedia;
import com.github.cyberedcake.hystats.utils.UChat;
import com.github.cyberedcake.hystats.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;

import java.util.Map;

public class Socials extends StatsCategoryCommand {

    public Socials() {
        super("socials", "socialMedia", "Checks the social media accounts of a certain Hypixel player.", "sm", "socialmedia", "media");
    }

    // socialMedia
    @Override
    public void execute(ICommandSender sender, GameStats stats, boolean oneLine, String[] args) {
        JsonObject object = stats.getObjectProperty("links");
        Map<String, String> map = new Gson().fromJson(object, new TypeToken<Map<String, String>>() {}.getType());
        if (object == null || map.isEmpty()) {
            send(stats.getUser() + " &chas no social media set!"); return;
        }

        send(stats.getUser() + "&f" + (stats.player().getName().endsWith("s") ? "'" : "'s") + " " + (oneLine ? "SM" : "Linked Social Media"));
        if (!oneLine) send(" ");

        for (Map.Entry<String, String> socialEntry : map.entrySet()) {
            String name = socialEntry.getKey();
            String link = socialEntry.getValue();
            SocialMedia social = SocialMedia.getSocial(name);
            boolean isLink = Utils.isUrl(link);

            IChatComponent component;

            HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, UChat.chat(
                    stats.getUser() + "&f'" + (stats.player().getName().endsWith("s") ? " " : "s ") + (social == null ? name : social.basic) + "&f: &b" + (isLink ? "&n" : "") + link + "\n\n" +
                            "&cThis account is not made or managed by Hypixel or Hystats!\n" +
                            "&cUse discretion when visiting external social media accounts." +
                            "\n\n" +
                            (isLink ? "&eClick here to open link!"
                                    : "&eClick here to paste in chat!")
            ));
            ClickEvent click = new ClickEvent(isLink ? ClickEvent.Action.OPEN_URL : ClickEvent.Action.SUGGEST_COMMAND, link);
            if (oneLine) {
                component = UChat.chat((social == null ? "&7" + name : social.shortened));
                component.getChatStyle()
                        .setChatHoverEvent(hover)
                        .setChatClickEvent(click);
            } else {
                component = UChat.chat((social == null ? "&7" + name : social.display) + "&f: &a" + link.replace("https://", ""));
                component.getChatStyle()
                        .setChatHoverEvent(hover)
                        .setChatClickEvent(click);
            }

            send(component);
        }
    }
}
