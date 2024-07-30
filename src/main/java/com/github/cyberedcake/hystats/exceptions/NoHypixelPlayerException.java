package com.github.cyberedcake.hystats.exceptions;

public class NoHypixelPlayerException extends HyStatsError {

    public NoHypixelPlayerException(String username) {
        super("Player " + username + " has never logged onto Hypixel before!");
    }

}
