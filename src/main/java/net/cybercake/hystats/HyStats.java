package net.cybercake.hystats;

import net.cybercake.hystats.commands.DebugCommand;
import net.cybercake.hystats.commands.stats.StatsCommandManager;
import net.cybercake.hystats.api.ApiManager;
import net.cybercake.hystats.hypixel.ranks.SpecialHypixelRank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.net.SocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mod(modid = "hystats", useMetadata=true)
public class HyStats {

    public static ApiManager hypixel;
    public static StatsCommandManager command;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        command = new StatsCommandManager();
        ClientCommandHandler.instance.registerCommand(command);
        ClientCommandHandler.instance.registerCommand(new DebugCommand());

        hypixel = new ApiManager();
        hypixel.reloadApi();

        try {
            SpecialHypixelRank.createSpecialHypixelRanks();
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    public static SocketAddress getConnectedServer() {
        return Minecraft.getMinecraft().getNetHandler().getNetworkManager().getRemoteAddress();
    }

    public static boolean isHypixel() {
        return getConnectedServer().toString().contains("mc.hypixel.net");
    }

    public static List<NetworkPlayerInfo> getOnlinePlayers() {
        return Minecraft.getMinecraft()
                .getNetHandler()
                .getPlayerInfoMap()
                .stream()
                .filter(Objects::nonNull)
                .filter(npi ->
                        npi.getPlayerTeam() == null
                        || (npi.getPlayerTeam() != null && !npi.getPlayerTeam().formatString("").toUpperCase().contains("NPC")))
                .collect(Collectors.toList());
    }

}
