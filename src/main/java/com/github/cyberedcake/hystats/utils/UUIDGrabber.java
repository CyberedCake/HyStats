package com.github.cyberedcake.hystats.utils;

import com.github.cyberedcake.hystats.exceptions.UuidNotExist;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDGrabber {

    private static final Map<String, UUID> cachedUsernames = new HashMap<>();

    public static UUID getUUIDOf(String username) {
        UUID uuid = cachedUsernames.get(username);
        if (uuid != null) return uuid;

        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        try {
            InputStream input = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
            String stringUuid = convertUUID(obj.get("id").getAsString());
            UUID returned = UUID.fromString(stringUuid);

            cachedUsernames.put(username, returned);

            return returned;
        } catch (Exception exception) {
            return null;
        }
    }

    public static String convertUUID(String uuid) {
        if (uuid == null || uuid.length() != 32) {
            throw new IllegalArgumentException("Invalid UUID format");
        }
        return uuid.substring(0, 8) + "-" +
                uuid.substring(8, 12) + "-" +
                uuid.substring(12, 16) + "-" +
                uuid.substring(16, 20) + "-" +
                uuid.substring(20);
    }

}
