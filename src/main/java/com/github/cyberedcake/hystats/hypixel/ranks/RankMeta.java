package com.github.cyberedcake.hystats.hypixel.ranks;

public class RankMeta {

    static RankMeta of(String text) {
        return new RankMeta(text);
    }

    private final String prefix;

    private RankMeta(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() { return this.prefix; }

}
