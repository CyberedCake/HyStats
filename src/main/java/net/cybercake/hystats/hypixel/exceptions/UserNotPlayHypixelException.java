package net.cybercake.hystats.hypixel.exceptions;

import java.util.UUID;

public class UserNotPlayHypixelException extends HyStatsError {

    public UserNotPlayHypixelException(int code, UUID uuid, String username) {
        super(code, "That player hasn't played on Hypixel before: &8" + (username != null ? username : uuid.toString()));
    }

}
