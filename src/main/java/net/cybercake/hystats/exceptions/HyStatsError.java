package net.cybercake.hystats.exceptions;

import net.cybercake.hystats.HyStats;

// 12 next number
public class HyStatsError extends RuntimeException {

    public HyStatsError(int code, String message) {
        super(message);
    }

    public HyStatsError(int code, Throwable cause) {
        super("&d|E:" + code, cause);
    }

    public HyStatsError(int code, String message, Throwable cause) {
        super(message + "&d|E:" + code, cause);
    }

}
