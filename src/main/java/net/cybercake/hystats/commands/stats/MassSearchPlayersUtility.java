package net.cybercake.hystats.commands.stats;

import com.mojang.authlib.GameProfile;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.Pair;
import net.cybercake.hystats.utils.UChat;
import net.cybercake.hystats.utils.records.ProcessedStatsOutput;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.cybercake.hystats.utils.UChat.format;
import static net.cybercake.hystats.utils.UChat.send;

public class MassSearchPlayersUtility {

    private final RequestProcessor processor;

    public MassSearchPlayersUtility(RequestProcessor processor) {
        this.processor = processor;
    }

    public void showAsParty(List<String> users, @Nullable Exception exception) {
        if (exception != null) {
            send(this.processor.params.manager.getError(exception, true));
            return;
        }

        if (users.isEmpty()) {
            send(format("&cYou are not currently in a party."));
            return;
        }

        send(format("&7&oLoading stats of " + users.size() + " player" + (users.size() == 1 ? "" : "s") + ", please wait...", null, false));

        this.processor.params
                .showUtilityMessages(false)
                .compact(true);

        List<ProcessedStatsOutput> componentStats = new ArrayList<>();
        for (String user : users) {
            ProcessedStatsOutput processed = this.processor.processRequest(user);
            componentStats.add(processed);
        }

        this.sendComponents(componentStats);
    }

    void showPlayers(List<GameProfile> players) {
        this.processor.params
                .showUtilityMessages(false)
                .compact(true);

        List<ProcessedStatsOutput> components = new ArrayList<>();
        for (GameProfile player : players) {
            ProcessedStatsOutput processed = this.processor.processRequest(player.getName());
            components.add(processed);
        }

        this.sendComponents(components);
    }

    private void sendComponents(List<ProcessedStatsOutput> components) {
        if (this.processor.params.processors.countActiveProcessors() != 0)
            components = this.processor.params.processors.streamList(components);

        send(UChat.separator());
        for (ProcessedStatsOutput pair : components) {
            send(pair.chat());
        }
        send(UChat.separator());
    }

}
