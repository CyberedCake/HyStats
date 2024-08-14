package com.github.cyberedcake.hystats.hypixel;

import com.github.cyberedcake.hystats.HyStatsMain;
import com.github.cyberedcake.hystats.exceptions.HyStatsError;
import com.github.cyberedcake.hystats.exceptions.NoHypixelPlayerException;
import com.github.cyberedcake.hystats.exceptions.NoUserException;
import com.github.cyberedcake.hystats.hypixel.ranks.HypixelRank;
import com.github.cyberedcake.hystats.utils.UUIDGrabber;
import com.github.cyberedcake.hystats.utils.Utils;
import net.hypixel.api.reply.PlayerReply;
import net.hypixel.api.reply.StatusReply;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CachedApiCall {

    private static final int EXPIRATION_TIME = 60; // seconds


    public static List<CachedApiCall> cached = new ArrayList<>();
    public static List<UUID> notExist = new ArrayList<>();

    public static boolean isCached(String username) {
        for (CachedApiCall call : cached) {
            if (
                    (Utils.isUuid(username) && call.uuid != null && call.uuid.toString().equalsIgnoreCase(username))
                ||
                    !username.equalsIgnoreCase(call.username)) continue;

            if (System.currentTimeMillis() - call.updated > EXPIRATION_TIME * 1_000) return false;

            if (call.error == null) return true;

            return true;
        }
        return false;
    }

    public static CachedApiCall grab(String username) throws ExecutionException, InterruptedException, TimeoutException, HyStatsError {
        CachedApiCall api;
        if (isCached(username)) {
            api = cached.stream().filter(c -> c.username != null && c.username.equalsIgnoreCase(username)).findFirst().orElseThrow(() -> new RuntimeException("Failed to retrieve cached data for " + username));
        } else {
            api = grab(username, Utils.isUuid(username) ? UUID.fromString(username) : UUIDGrabber.getUUIDOf(username));
        }

        if (api.error != null)
            throw api.error;

        return api;
    }

    public static CachedApiCall grab(String username, UUID uuid) throws ExecutionException, InterruptedException, TimeoutException, HyStatsError {
        System.out.println("Found uncached api call, calling a new one...");
        CachedApiCall api = null;
        for (CachedApiCall call : cached) {
            if (uuid == null) break;
            if (call.uuid == null) continue;
            if (!uuid.equals(call.uuid)) continue;

            api = (System.currentTimeMillis() - call.updated <= EXPIRATION_TIME * 1_000) ? call : null;
            break;
        }

        if (api == null) {
            api = new CachedApiCall(null, uuid, null, null, null);
        }

        if (uuid == null) {
            api.error = new NoUserException(username);
            api.username = username;
            cache(api);
            throw api.error;
        }

        System.out.println("Calling Hypixel API...");
        PlayerReply playerReply = HyStatsMain.API.getPlayerByUuid(uuid).get(20, TimeUnit.SECONDS);
        StatusReply statusReply = HyStatsMain.API.getStatus(uuid).get(20, TimeUnit.SECONDS);

        if (!playerReply.getPlayer().exists()) {
            api.error = new NoHypixelPlayerException(username);
            api.username = username;
            notExist.add(uuid);
            cache(api);
            throw api.error;
        }

        System.out.println("Received response: player=" + playerReply.isSuccess() + ", status=" + statusReply.isSuccess());

        api.player = playerReply.getPlayer();
        api.username = api.player.getName();
        api.session = statusReply.getSession();
        api.displayName = HypixelRank.getRank(api.player).format(api.player);
        api.updated = System.currentTimeMillis();

        cache(api);

        return api;
    }

    static void cache(CachedApiCall newCall) {
        for (CachedApiCall cache : new ArrayList<>(cached)) {
            if (cache.uuid == null) continue;
            if (!cache.uuid.equals(newCall.uuid)) continue;
            cached.remove(cache);
            break;
        }

        cached.add(newCall);

        System.out.println("Cached data for " + newCall.uuid + " (" + newCall.username + ")");
    }


    public final UUID uuid;
    public String username;
    public String displayName;
    public PlayerReply.Player player;
    public StatusReply.Session session;

    public HyStatsError error;

    public long updated;

    private CachedApiCall(String username, UUID uuid, String displayName, PlayerReply.Player player, StatusReply.Session session) {
        this.username = username;
        this.uuid = uuid;
        this.displayName = displayName;
        this.player = player;
        this.session = session;

        this.updated = System.currentTimeMillis();
    }

}
