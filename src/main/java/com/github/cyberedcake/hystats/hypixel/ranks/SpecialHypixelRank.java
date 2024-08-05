package com.github.cyberedcake.hystats.hypixel.ranks;

import com.github.cyberedcake.hystats.exceptions.HyStatsError;
import com.github.cyberedcake.hystats.exceptions.SpecialRanksFailureException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpecialHypixelRank {

    private static final String specialRanks = "https://raw.githubusercontent.com/CyberedCake/HyStats/master/src/main/resources/special_ranks.json";

    private static final List<SpecialHypixelRank> ranks = new ArrayList<>();

    public static SpecialHypixelRank rankOf(UUID uuid) {
        if (ranks.isEmpty()) return null;
        return ranks.stream().filter(s -> s.uuid.equals(uuid)).findFirst().orElse(null);
    }

    public static void createSpecialHypixelRanks() throws HyStatsError {
        if (!ranks.isEmpty()) ranks.clear();
        try {
            URL url = new URI(specialRanks).toURL();

            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            JsonArray array = JsonParser.parseReader(bufferedReader).getAsJsonArray();

            for (JsonElement element : array.asList()) {
                JsonObject obj = element.getAsJsonObject();

                UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
                String username = obj.get("name").getAsString();
                String prefix = obj.get("rank").getAsString();

                SpecialHypixelRank rank = new SpecialHypixelRank(uuid, username, prefix);

                ranks.add(rank);
            }
        } catch (Exception exception) {
            throw new SpecialRanksFailureException(exception);
        }
    }

    private final UUID uuid;
    private final String username;
    private final String prefix;

    private SpecialHypixelRank(UUID uuid, String username, String prefix) {
        this.uuid = uuid;
        this.username = username;
        this.prefix = prefix;
    }

    public String getPrefix() { return this.prefix; }

}
