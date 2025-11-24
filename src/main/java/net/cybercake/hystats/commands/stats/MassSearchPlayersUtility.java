package net.cybercake.hystats.commands.stats;

import com.mojang.authlib.GameProfile;
import net.cybercake.hystats.hypixel.GameStats;
import net.cybercake.hystats.utils.Pair;
import net.cybercake.hystats.utils.UChat;
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

        Map<IChatComponent, GameStats> componentStats = new HashMap<>();
        for (String user : users) {
            componentStats.putAll(this.processor.processRequest(user).asSmallMap());
        }

        if (this.processor.params.processors.countActiveProcessors() != 0)
            componentStats = this.processor.params.processors.streamList(componentStats);

        send(UChat.separator());
        componentStats.keySet().forEach(UChat::send);
        send(UChat.separator());
    }

    void showPlayers(List<GameProfile> players) {
        this.processor.params
                .showUtilityMessages(false)
                .compact(true);
        System.out.println("Will be processing many players here shortly");

        Map<IChatComponent, GameStats> components = new HashMap<>();
        for (GameProfile player : players) {
            Map<IChatComponent, GameStats> temp = this.processor.processRequest(player.getName());
            System.out.println("Now in loop, temp=" + temp);
            components.putAll(temp);
            System.out.println("Complete components=" + components);
        }

        if (this.processor.params.processors.countActiveProcessors() != 0)
            components = this.processor.params.processors.streamList(components);

        send(UChat.separator());
        components.keySet().forEach(UChat::send);
        send(UChat.separator());
    }

}
