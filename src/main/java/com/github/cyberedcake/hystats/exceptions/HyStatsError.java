package com.github.cyberedcake.hystats.exceptions;

public class HyStatsError extends Exception {

    public HyStatsError(String message) {
        super(message);
        System.out.println("An error occurred within HyStats: " + this.toString());
        System.out.println("(" + message + ")");
    }

    public HyStatsError(String message, Exception cause) {
        super(message, cause);
        System.out.println("An error occurred within HyStats: " + this.toString() + " caused by " + cause.toString());
        System.out.println("(" + message + ")");
    }

}
