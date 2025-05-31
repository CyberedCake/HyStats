package net.cybercake.hystats.hypixel;

import net.cybercake.hystats.api.ApiManager;
import net.cybercake.hystats.commands.flags.Arguments;
import net.cybercake.hystats.exceptions.HyStatsError;
import net.cybercake.hystats.exceptions.UserNotPlayHypixelException;
import net.cybercake.hystats.exceptions.UnusualApiResponse;
import net.cybercake.hystats.exceptions.UserNotExistException;
import net.cybercake.hystats.hypixel.ranks.HypixelRank;
import net.hypixel.api.reply.GuildReply;
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
    @Nullable GuildReply guildReply;

    @Nullable String username;
    @Nullable String displayName;

    public CachedPlayer(ApiManager api, UUID uuid) {
        this(api, uuid, null);
    }

    public CachedPlayer(ApiManager api, UUID uuid, @Nullable String username) {
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

    public GameStats asGameStats(String prefix, Arguments args) {
        return new GameStats(this, args, prefix);
    }

    <T> T notNull(T obj) {
        if (obj == null) {
            throw new UserNotPlayHypixelException(4, this.uuid, this.username);
        }
        return obj;
    }

    public void grab() {
        try {
            this.lastReply = System.currentTimeMillis();

            PlayerReply player = this.api.hypixel().getPlayerByUuid(uuid).get(5, TimeUnit.SECONDS);
            StatusReply status = this.api.hypixel().getStatus(uuid).get(5, TimeUnit.SECONDS);
            GuildReply guild = this.api.hypixel().getGuildByPlayer(uuid).get(5, TimeUnit.SECONDS);

            if (!player.getPlayer().exists()) {
                throw new UserNotPlayHypixelException(1, this.uuid, this.username);
            }

            this.playerReply = player;
            this.statusReply = status;
            this.guildReply = guild;

            this.displayName = HypixelRank.getRank(this.playerReply.getPlayer()).format(this.playerReply.getPlayer());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new UnusualApiResponse(e);
        } catch (UserNotPlayHypixelException e) {
            throw e;
        } catch (Exception exception) {
            throw new HyStatsError(2, exception.toString());
        }
    }

}
