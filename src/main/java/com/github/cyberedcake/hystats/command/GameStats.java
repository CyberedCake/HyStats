package com.github.cyberedcake.hystats.command;

import com.google.gson.JsonObject;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GameStats {

    private final @Nullable String prefix;

    private final String userDisplayName;
    private final PlayerReply.Player player;
    private final StatusReply.Session session;

    GameStats(@Nullable String prefix, PlayerReply.Player player, StatusReply.Session session, String userDisplayName) {
        this.prefix = prefix;
        this.player = player;
        this.session = session;
        this.userDisplayName = userDisplayName;
    }

    public int getIntProperty(String property) {
        return this.getIntProperty(property, -1);
    }

    public int getIntProperty(String property, int def) {
        return this.player.getIntProperty(concat(property), def);
    }

    public double getDoubleProperty(String property) {
        return this.getDoubleProperty(property, -1);
    }

    public double getDoubleProperty(String property, double def) {
        return this.player.getDoubleProperty(concat(property), def);
    }

    public JsonObject getObjectProperty(String property) {
        return this.player.getObjectProperty(concat(property));
    }

    public String getProperty(String property) {
        return this.getProperty(property, null);
    }

    public String getProperty(String property, String def) {
        return this.player.getStringProperty(concat(property), def);
    }


    public String getUser() { return this.userDisplayName; }
    public PlayerReply.Player player() { return this.player; }
    public StatusReply.Session session() { return this.session; }
    public UUID getUUID() { return this.player.getUuid(); }




    private String concat(String property) {
        if (this.prefix == null) return property;
        return this.prefix + "." + property;
    }

}
