package net.cybercake.hystats.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import net.cybercake.hystats.exceptions.UserNotExistException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class UUIDUtils {

    private static final Map<String, UUID> cachedUsernames = new HashMap<>();

    public static UUID getUUIDOf(String username) {
        // method one: check cache
        UUID uuid = cachedUsernames.get(username);
        if (uuid != null) return uuid;

        // we know that the username was cached and we already knew about the fact it didn't exist
        if (cachedUsernames.containsKey(username)) {
            throw new UserNotExistException(6, username);
        }

        // method two: search players in lobby
        uuid = Minecraft.getMinecraft()
                .getNetHandler()
                .getPlayerInfoMap()
                .stream()
                .filter(Objects::nonNull)
                .map(NetworkPlayerInfo::getGameProfile)
                .filter(profile -> profile.getName().equalsIgnoreCase(username))
                .map(GameProfile::getId)
                .findFirst()
                .orElse(null);
        if (uuid != null) {
            cachedUsernames.putIfAbsent(username, uuid);
            return uuid;
        }

        // method three: finally, if all else fails, fetch from api.mojang.com
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        try {
            URL realUrl = new URL(url);
            InputStream input = realUrl.openStream();
            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(inputReader);

            JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
            String stringUuid = convertUUID(obj.get("id").getAsString());
            UUID returned = UUID.fromString(stringUuid);

            cachedUsernames.put(username, returned);

            return returned;
        } catch (Exception exception) {
            cachedUsernames.put(username, null);
            throw new UserNotExistException(6, username, exception);
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
