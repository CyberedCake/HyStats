package net.cybercake.hystats.hypixel.exceptions;

import java.util.UUID;

public class UserNotExistException extends HyStatsError {

    public UserNotExistException(int code, UUID uuid) {
        this(code, "[UUID " + uuid.toString() + "]");
    }

    public UserNotExistException(int code, String username) {
        super(code, "That player does not exist: &8" + username);
    }

}
