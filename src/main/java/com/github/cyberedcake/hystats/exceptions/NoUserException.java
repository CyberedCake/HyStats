package com.github.cyberedcake.hystats.exceptions;

public class NoUserException extends HyStatsError {

    public NoUserException(String username) {
        super("Username '" + username + "' does not exist!");
    }

}
