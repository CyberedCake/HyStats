package net.cybercake.hystats.api;

import com.google.common.collect.ImmutableList;
import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.hypixel.CachedPlayer;
import net.cybercake.hystats.utils.UChat;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;
import net.hypixel.api.reply.PlayerReply;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ApiManager {

    public static final long EXPIRATION_SECONDS = 60;

    private HypixelAPI hypixelApi;

    private ApiKey key;
    private int reloads;
    private final List<CachedPlayer> players;

    public ApiManager() {
        this.key = null;
        this.reloads = 0;
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
        this.print("Checking for Hypixel API key...", System.out);
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

        this.print("Discovered API key, establishing connection to API...", System.out);
//        System.out.println("Key: " + apiKey);

        try {
            HypixelHttpClient client = new ApacheHttpClient(UUID.fromString(this.key.getApiKey()));
            this.hypixelApi = new HypixelAPI(client);
            this.print("Connection established.", System.out);

            // test to ensure API is working by finding test players
            String testPlayer = ImmutableList.of(
                            "f7c77d99-9f15-4a66-a87d-c4a51ef30d19", // simon hypixel
                            "9b2a30ec-f8b3-4dfe-bf49-9c5c367383f8", // rezzus
                            "b876ec32-e396-476b-a115-8438d83c67d4" // technoblade (never dies)
                    )
                    .get(this.reloads % 3);
            this.print("Testing API key... (" + testPlayer + ")", System.out);
            PlayerReply.Player player = this.hypixelApi
                    .getPlayerByUuid(testPlayer)
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

        this.reloads++;
        this.players.clear(); // clear existing stored players from cache to make way for new ones (in case of reload)
        print("Hypixel's API has been loaded with a valid key in " + (System.currentTimeMillis() - mss) + "ms!", System.out);
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
        this.print(UChat.repeat("-", 50), System.err);
        this.print("HYSTATS FAILURE!", System.err);
        for (String line : additionalMessage) {
            this.print(line, System.err);
        }
        this.print(UChat.repeat("-", 50), System.err);
        this.hypixelApi = null;
    }

    private void print(String msg, PrintStream stream) {
        if (HyStats.getConnectedServer() != null) {
            try {
                if (System.err.equals(stream)) {
                    UChat.send(UChat.format("&c&o" + msg));
                } else {
                    UChat.send(UChat.format("&7&o" + msg));
                }
            } catch (Exception ignored) {
                // who cares, this is a very debug-y feature anyways.
            }
        }
        stream.println(msg);
    }


}
