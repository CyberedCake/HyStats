package net.cybercake.hystats.utils.records;

import net.cybercake.hystats.hypixel.GameStats;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;

public class ProcessedStatsOutput {

    public static ProcessedStatsOutput of(GameStats stats, IChatComponent chat) {
        return new ProcessedStatsOutput(stats, chat);
    }

    public static ProcessedStatsOutput of(IChatComponent chat, GameStats stats) {
        return of(stats, chat);
    }

    private final GameStats stats;
    private final IChatComponent chat;

    private ProcessedStatsOutput(GameStats stats, IChatComponent chat) {
        this.stats = stats;
        this.chat = chat;
    }

    public GameStats stats() { return this.stats; }
    public IChatComponent chat() { return this.chat; }
}
