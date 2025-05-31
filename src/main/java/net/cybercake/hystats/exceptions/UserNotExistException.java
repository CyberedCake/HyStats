package net.cybercake.hystats.exceptions;

import java.util.UUID;

public class UserNotExistException extends HyStatsError {

    public UserNotExistException(int code, UUID uuid) {
        this(code, "[UUID " + uuid.toString() + "]");
    }

    public UserNotExistException(int code, String username) {
        super(code, "That player does not exist: &8" + username);
    }

    public UserNotExistException(int code, UUID uuid, Throwable cause) {
        this(code, "[UUID " + uuid.toString() + "]", cause);
    }

    public UserNotExistException(int code, String username, Throwable cause) {
        super(code, "That player does not exist: &8" + username, cause);
    }

}
