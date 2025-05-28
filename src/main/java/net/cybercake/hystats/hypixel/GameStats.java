package net.cybercake.hystats.hypixel;

import com.google.gson.JsonObject;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;

import javax.annotation.Nullable;
import java.util.UUID;

public class GameStats {

    private final CachedPlayer player;
    private final String prefix;

    GameStats(CachedPlayer player, @Nullable String prefix) {
        this.player = player;
        this.prefix = prefix;
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

    public String getUser() { return this.player.displayName; }
    public PlayerReply.Player player() { return this.player.notNull(this.player.playerReply).getPlayer(); }
    public StatusReply.Session session() { return this.player.notNull(this.player.statusReply).getSession(); }
    public UUID getUUID() { return this.player.getUniqueId(); }




    private String concat(String property) {
        if (this.prefix == null) return property;
        return this.prefix + "." + property;
    }

}
