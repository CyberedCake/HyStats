package net.cybercake.hystats.api;

import net.cybercake.hystats.hypixel.CachedPlayer;
import net.cybercake.hystats.utils.UChat;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.reply.PlayerReply;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ApiManager {

    public static final long EXPIRATION_SECONDS = 60;

    private HypixelAPI hypixelApi;

    private ApiKey key;
    private final List<CachedPlayer> players;

    public ApiManager() {
        this.key = null;
        this.players = new ArrayList<>();
    }


    public CachedPlayer getPlayer(UUID uuid) {
        return this.getPlayer(uuid, null);
    }

    public CachedPlayer getPlayer(UUID uuid, String username) {
        CachedPlayer existing = players.stream()
                .filter(c -> c.getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            return existing;
        }
        existing = new CachedPlayer(this, uuid, username);
        this.players.add(existing);
        return existing;
    }

    public void reloadApi() {
        long mss = System.currentTimeMillis();
        System.out.println("Checking for Hypixel API key...");
        if (this.key == null || !this.key.isCustom()) {
            this.key = new ApiKey("hypixel.api", "HYPIXEL_API_KEY");
        }

        if (this.key == null) {
            this.fail(
                    "Failed to find a stored API key for Hypixel. HyStats cannot function without an API key.",
                    "Searched: java property 'hypixel.api', env 'HYPIXEL_API_KEY'"
            );
            return;
        }

        System.out.println("Discovered API key, proceeding with HyStats start-up...");
//        System.out.println("Key: " + apiKey);

        try {
            HypixelHttpClient client = new ApacheHttpClient(UUID.fromString(this.key.getApiKey()));
            this.hypixelApi = new HypixelAPI(client);

            // test to ensure API is working by finding Simon hypixel's player
            PlayerReply.Player player = this.hypixelApi
                    .getPlayerByUuid(UUID.fromString("f7c77d99-9f15-4a66-a87d-c4a51ef30d19"))
                    .get(20, TimeUnit.SECONDS)
                    .getPlayer();

            if (!player.exists()) {
                throw new IllegalStateException("Accessed Hypixel API but received unexpected response.");
            }
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            this.fail(
                    "Failed to initialize a connection with the Hypixel API. " +
                            "HyStats cannot function without this connection.",
                    exception.toString()
            );
            return;
        }

        this.players.clear(); // clear existing stored players from cache to make way for new ones (in case of reload)
        System.out.println("Hypixel's API has been loaded with a valid key in " + (System.currentTimeMillis() - mss) + "ms!");
    }

    public boolean isApiEnabled() {
        return this.hypixelApi != null;
    }

    public void setApiKey(String key) {
        this.key = new ApiKey(key, true);
    }

    public HypixelAPI hypixel() {
        return this.hypixelApi;
    }

    private void fail(String... additionalMessage) {
        System.err.println(UChat.repeat("-", 50));
        System.err.println("HYSTATS FAILURE!");
        for (String line : additionalMessage) {
            System.err.println(line);
        }
        System.err.println(UChat.repeat("-", 50));
        this.hypixelApi = null;
    }


}
