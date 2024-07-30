package com.github.cyberedcake.hystats.exceptions;

import java.util.UUID;

public class UuidNotExist extends HyStatsError {

    public UuidNotExist(String username) {
        super("Username " + username + " does not exist as a Minecraft account!");
    }

}
