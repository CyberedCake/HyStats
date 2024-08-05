package com.github.cyberedcake.hystats.hypixel;

public enum SocialMedia {
    YOUTUBE("&cY&fT", "&cYou&fTube", "YouTube"),
    TWITTER("&bTW", "&bTwitter", "Twitter"),
    TWITCH("&5T.TV", "&5Twitch", "Twitch"),
    DISCORD("&3DISC", "&3Discord", "Discord"),
    INSTAGRAM("&dI&6T&eG", "&dIns&6tag&eram", "Instagram"),
    HYPIXEL("&6HY", "&6Hypixel Forums", "Hypixel Forums"),
    TIKTOK("&bT&cT", "&bTik&cTok", "TikTok"),
    MIXER("&fM&3I&bX", "&fMi&3x&ber", "Mixer");

    public static SocialMedia getSocial(String input) {
        try {
            return valueOf(input);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public final String shortened;
    public final String display;
    public final String basic;

    SocialMedia(String shortened, String display, String basic) {
        this.shortened = shortened;
        this.display = display;
        this.basic = basic;
    }

}
