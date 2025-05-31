package net.cybercake.hystats.commands.stats;

import com.mojang.authlib.GameProfile;
import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.commands.flags.Arguments;
import net.cybercake.hystats.utils.UChat;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.cybercake.hystats.utils.UChat.format;
import static net.cybercake.hystats.utils.UChat.send;

public class MassSearchPlayersUtility {

    private final RequestProcessor processor;

    public MassSearchPlayersUtility(RequestProcessor processor) {
        this.processor = processor;
    }

    public void findAllInParty(List<String> users, @Nullable Exception exception) {
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

        List<IChatComponent> components = new ArrayList<>();
        for (String user : users) {
            components.add(this.processor.processRequest(user));
        }

        send(UChat.separator());
        components.forEach(UChat::send);
        send(UChat.separator());
    }

    void findAllInLobby() {
        List<GameProfile> players = HyStats.getOnlinePlayers().stream().map(NetworkPlayerInfo::getGameProfile).limit(24).collect(Collectors.toList());
        UChat.send(format(
                "&7&oLoading stats of " + players.size() + " player" + (players.size() == 1 ? "" : "s") + ", please wait..."
        ));

        this.processor.params
                .showUtilityMessages(false)
                .compact(true);

        List<IChatComponent> components = new ArrayList<>();
        for (GameProfile player : players) {
            components.add(this.processor.processRequest(player.getName()));
        }

        send(UChat.separator());
        components.forEach(UChat::send);
        send(UChat.separator());
    }

}
