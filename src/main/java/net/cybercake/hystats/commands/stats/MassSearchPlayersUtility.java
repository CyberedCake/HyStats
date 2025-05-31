package net.cybercake.hystats.commands.stats;

import com.mojang.authlib.GameProfile;
import net.cybercake.hystats.utils.UChat;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

        List<IChatComponent> components = new ArrayList<>();
        for (String user : users) {
            components.add(this.processor.processRequest(user));
        }

        send(UChat.separator());
        components.forEach(UChat::send);
        send(UChat.separator());
    }

    void showPlayers(List<GameProfile> players) {
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
