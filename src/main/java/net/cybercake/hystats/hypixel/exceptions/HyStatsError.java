package net.cybercake.hystats.hypixel.exceptions;

// 9 next number
public class HyStatsError extends RuntimeException {

    public HyStatsError(int code, String message) {
        super(message + "&d|E:" + code);
    }

    public HyStatsError(int code, Throwable cause) {
        super("&d|E:" + code, cause);
    }

    public HyStatsError(int code, String message, Throwable cause) {
        super(message + "&d|E:" + code, cause);
    }

}
