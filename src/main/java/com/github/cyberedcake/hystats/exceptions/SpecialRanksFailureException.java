package com.github.cyberedcake.hystats.exceptions;

public class SpecialRanksFailureException extends HyStatsError {

    public SpecialRanksFailureException(Exception cause) {
        super("Failed to load special ranks: " + cause);
    }

}
