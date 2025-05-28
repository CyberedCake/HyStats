package net.cybercake.hystats.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cybercake.hystats.hypixel.exceptions.UserNotExistException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDUtils {

    private static final Map<String, UUID> cachedUsernames = new HashMap<>();

    public static UUID getUUIDOf(String username) {
        UUID uuid = cachedUsernames.get(username);
        if (uuid != null) return uuid;

        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        try {
            InputStream input = new URL(url).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            String stringUuid = convertUUID(obj.get("id").getAsString());
            UUID returned = UUID.fromString(stringUuid);

            cachedUsernames.put(username, returned);

            return returned;
        } catch (Exception exception) {
            return null;
        }
    }

    private static String convertUUID(String uuid) {
        if (uuid == null || uuid.length() != 32) {
            throw new IllegalArgumentException("Invalid UUID format");
        }
        return uuid.substring(0, 8) + "-" +
                uuid.substring(8, 12) + "-" +
                uuid.substring(12, 16) + "-" +
                uuid.substring(16, 20) + "-" +
                uuid.substring(20);
    }

    public static UUID processUUID(String fromString) {
        if (isUUID(fromString)) {
            return UUID.fromString(fromString);
        }
        UUID returned = getUUIDOf(fromString);
        if (returned == null) {
            throw new UserNotExistException(5, fromString);
        }
        return returned;
    }

    public static boolean isUUID(String text) {
        try {
            UUID.fromString(text);
            return true;
        } catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
    }

}
