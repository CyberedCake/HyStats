package net.cybercake.hystats.hypixel.exceptions;

public class HyStatsError extends RuntimeException {

    public HyStatsError(int code, String message) {
        super(message + "&d|E:" + code);
    }

    public HyStatsError(int code, Throwable cause) {
        super("&d|E:" + code, cause);
    }

}
