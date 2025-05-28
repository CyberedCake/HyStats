package net.cybercake.hystats.hypixel;

import net.cybercake.hystats.hypixel.exceptions.HyStatsError;
import net.cybercake.hystats.hypixel.exceptions.NonExistentApiResponse;
import net.cybercake.hystats.hypixel.exceptions.UnusualApiResponse;
import net.cybercake.hystats.hypixel.exceptions.UserNotExistException;
import net.cybercake.hystats.hypixel.ranks.HypixelRank;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CachedPlayer {

    private final ApiManager api;
    private final UUID uuid;

    long lastReply;
    @Nullable PlayerReply playerReply;
    @Nullable StatusReply statusReply;

    @Nullable String username;
    @Nullable String displayName;

    CachedPlayer(ApiManager api, UUID uuid) {
        this(api, uuid, null);
    }

    CachedPlayer(ApiManager api, UUID uuid, String username) {
        if (uuid == null) {
            throw new UserNotExistException(0, "[no user input]");
        }

        this.api = api;
        this.uuid = uuid;

        this.lastReply = 0L;
        this.playerReply = null;
        this.statusReply = null;

        this.username = this.displayName = username;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public boolean isExpired() {
        return
                this.lastReply == 0 ||
                System.currentTimeMillis() - this.lastReply > ApiManager.EXPIRATION_SECONDS * 1_000L;
    }

    public GameStats asGameStats(String prefix) {
        return new GameStats(this, prefix);
    }

    <T> T notNull(T obj) {
        if (obj == null) {
            throw new NonExistentApiResponse();
        }
        return obj;
    }

    public void grab() {
        try {
            this.lastReply = System.currentTimeMillis();

            PlayerReply player = this.api.hypixel().getPlayerByUuid(uuid).get(5, TimeUnit.SECONDS);
            StatusReply status = this.api.hypixel().getStatus(uuid).get(5, TimeUnit.SECONDS);

            if (!player.getPlayer().exists()) {
                if (this.username != null) {
                    throw new UserNotExistException(1, this.username);
                }
                throw new UserNotExistException(1, this.uuid);
            }

            this.playerReply = player;
            this.statusReply = status;
            this.displayName = HypixelRank.getRank(this.playerReply.getPlayer()).format(this.playerReply.getPlayer());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new UnusualApiResponse(e);
        } catch (Exception exception) {
            throw new HyStatsError(2, exception.toString());
        }
    }

}
