package net.cybercake.hystats.hypixel;

import net.cybercake.hystats.HyStats;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApiManager {

    public static final String API_KEY = "50c122bb-0fc4-40c4-94a3-4a7fce044f5f";
    public static final long EXPIRATION_SECONDS = 60;

    private HypixelAPI hypixelApi;

    private final List<CachedPlayer> players;

    public ApiManager() {
        this.players = new ArrayList<>();
    }

    public CachedPlayer getPlayer(UUID uuid) {
        return this.getPlayer(uuid, null);
    }

    public CachedPlayer getPlayer(UUID uuid, String username) {
        CachedPlayer existing = players.stream().filter(c -> c.getUniqueId().equals(uuid)).findFirst().orElse(null);
        if (existing != null) {
            return existing;
        }
        existing = new CachedPlayer(this, uuid, username);
        this.players.add(existing);
        return existing;
    }

    public void reloadApi() {
        HypixelHttpClient client = new ApacheHttpClient(UUID.fromString(API_KEY));
        hypixelApi = new HypixelAPI(client);
        System.out.println("Hypixel's API has been loaded for " + HyStats.class.getCanonicalName());
    }

    public boolean isApiEnabled() {
        return this.hypixelApi != null;
    }

    HypixelAPI hypixel() {
        return this.hypixelApi;
    }


}
