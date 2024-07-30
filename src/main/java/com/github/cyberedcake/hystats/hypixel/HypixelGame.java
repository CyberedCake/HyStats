package com.github.cyberedcake.hystats.hypixel;

import org.jetbrains.annotations.Nullable;

public enum HypixelGame {

    //<editor-fold desc="Games (Updated July 30th, 2024)">
    QUAKECRAFT(2, "Quake", "Quake", "Quakecraft"),

    WALLS(3, "Walls", "Walls"),

    PAINTBALL(4, "Paintball", "Paintball"),

    SURVIVAL_GAMES(5, "HungerGames", "Blitz Survival Games"),

    TNTGAMES(6, "TNTGames", "TNT Games"),

    VAMPIREZ(7, "VampireZ", "VampireZ"),

    WALLS3(13, "Walls3", "Mega Walls"),

    ARCADE(14, "Arcade", "Arcade", "Arcade Games"),

    ARENA(17, "Arena", "Arena", "Arena Brawl"),

    UHC(20, "UHC", "UHC Champions"),

    MCGO(21, "MCGO", "Cops and Crims"),

    BATTLEGROUND(23, "Battleground", "Warlords"),

    SUPER_SMASH(24, "SuperSmash", "Smash Heroes"),

    GINGERBREAD(25, "GingerBread", "Turbo Kart Racers"),

    HOUSING(26, "Housing", "Housing"),

    SKYWARS(51, "SkyWars", "SkyWars"),

    TRUE_COMBAT(52, "TrueCombat", "Crazy Walls"),

    SPEED_UHC(54, "SpeedUHC", "Speed UHC"),

    SKYCLASH(55, "SkyClash", "SkyClash"),

    LEGACY(56, "Legacy", "Classic Games"),

    PROTOTYPE(57, "Prototype", "Prototype", "Prototype Games"),

    BEDWARS(58, "Bedwars", "Bed Wars"),

    MURDER_MYSTERY(59, "MurderMystery", "Murder Mystery"),

    BUILD_BATTLE(60, "BuildBattle", "Build Battle"),

    DUELS(61, "Duels", "Duels"),

    SKYBLOCK(63, "SkyBlock", "SkyBlock"),

    PIT(64, "Pit", "Pit", "The Hypixel Pit"),

    REPLAY(65, "Replay", "Replay", "watching:a Replay"),

    SMP(67, "SMP", "SMP", "Survival"),

    WOOL_GAMES(68, "WoolGames", "Wool Wars");
    //</editor-fold>


    final int id;
    final String typeName;
    final String databaseName;
    final String cleanName;
    final String action;
    final String displayName;

    HypixelGame(int id, String databaseName, String cleanName) {
        this(id, databaseName, cleanName, null);
    }

    HypixelGame(int id, String databaseName, String cleanName, @Nullable String displayName) {
        this.id = id;
        this.typeName = this.name();
        this.databaseName = databaseName;
        this.cleanName = cleanName;
        this.action = (displayName == null ? "playing" :
                (displayName.contains(":") ? displayName.split(":")[0] : "playing")
        );
        this.displayName = (displayName == null ? this.cleanName :
                (displayName.contains(":") ? displayName.split(":")[1] : displayName)
        );
    }

    public static String getFormattedGame(String input) {
        HypixelGame game = getGame(input);
        if (game == null ) return null;
        return game.action + " " + game.displayName;
    }

    public static @Nullable HypixelGame getGame(String input) {
        try {
            return HypixelGame.valueOf(input);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

}
