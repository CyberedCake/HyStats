package net.cybercake.hystats.hypixel;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.commands.flags.Arguments;
import net.cybercake.hystats.commands.flags.CommandArgument;
import net.cybercake.hystats.utils.ColorCode;
import net.cybercake.hystats.utils.TriState;
import net.cybercake.hystats.utils.UChat;
import net.hypixel.api.reply.GuildReply;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;
import java.util.UUID;

import static net.cybercake.hystats.utils.UChat.format;

public class GameStats {

    private final CachedPlayer player;
    private final Arguments args;
    private final String apiPrefix;

    private String displayName;

    GameStats(CachedPlayer player, Arguments args, @Nullable String apiPrefix) {
        this.player = player;
        this.args = args;
        this.apiPrefix = apiPrefix;

        CommandArgument arg = this.args.arg("displayname", "dn");
        this.displayName = this.player.displayName;
        if (arg.exists()) {
            HyStats.getOnlinePlayers()
                    .stream()
                    .filter(npi -> npi.getGameProfile().getId().equals(this.getUUID()))
                    .findFirst()
                    .ifPresent(networkPlayerInfo ->
                            this.displayName =
                                    (networkPlayerInfo.getDisplayName() != null
                                        ? networkPlayerInfo.getDisplayName().getFormattedText()
                                        : (
                                            networkPlayerInfo.getPlayerTeam() != null
                                                ? networkPlayerInfo.getPlayerTeam().formatString(this.player.username)
                                                : networkPlayerInfo.getGameProfile().getName()
                                        )
                                    )
                    );
        }
    }

    public int getIntProperty(String property) {
        return this.getIntProperty(property, -1);
    }

    public int getIntProperty(String property, int def) {
        return this.player().getIntProperty(concat(property), def);
    }

    public double getDoubleProperty(String property) {
        return this.getDoubleProperty(property, -1);
    }

    public double getDoubleProperty(String property, double def) {
        return this.player().getDoubleProperty(concat(property), def);
    }

    public JsonObject getObjectProperty(String property) {
        return this.player().getObjectProperty(concat(property));
    }

    public String getProperty(String property) {
        return this.getProperty(property, null);
    }

    public String getProperty(String property, String def) {
        return this.player().getStringProperty(concat(property), def);
    }

    public String getUser() {
        return this.displayName;
    }

    public TriState isStaffStatsHidden() {
        return this.player.staffHidden;
    }

    public IChatComponent getUserWithGuild() {
        IChatComponent username = format(this.getUser());
        IChatComponent guildTag = format("");
        if (this.guild() != null) {
            try {
                guildTag = format("&" + ColorCode.getColor(this.guild().getTagColor()).getCode() + " [" + this.guild().getTag() + "]");
                guildTag.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hystats " + this.getUUID() + " guild"));
                guildTag.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        format("&eClick here to view " + this.getUser() + "&e'" + (this.getUser().endsWith("s") ? "" : "s") + " guild, &b" + this.guild().getName() + "&e!")));
            } catch (Exception exception) {
                // anything goes wrong, default back to original
                guildTag = format("");
            }
        }
        return username.appendSibling(guildTag);
    }

    public PlayerReply.Player player() { return this.player.notNull(this.player.playerReply).getPlayer(); }
    public StatusReply.Session session() { return this.player.notNull(this.player.statusReply).getSession(); }
    public GuildReply.Guild guild() { return this.player.guildReply != null ? this.player.guildReply.getGuild() : null; }

    public String getUsername() { return this.player.username; }
    public UUID getUUID() { return this.player.getUniqueId(); }




    private String concat(String property) {
        if (this.apiPrefix == null) return property;
        return this.apiPrefix + "." + property;
    }

}
