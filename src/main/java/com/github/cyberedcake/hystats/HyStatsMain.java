package com.github.cyberedcake.hystats;

import com.github.cyberedcake.hystats.command.DebugCommand;
import com.github.cyberedcake.hystats.command.StatsCommand;
import com.github.cyberedcake.hystats.hypixel.ranks.SpecialHypixelRank;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.UUID;

@SuppressWarnings("CallToPrintStackTrace")
@Mod(modid = "hystats", useMetadata=true)
public class HyStatsMain {

    public static final String API_KEY = "38d23ee7-1dac-4a97-971a-38f8c8628cc1";
    public static HypixelAPI API;

    static { loadApi(); }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new StatsCommand());
        ClientCommandHandler.instance.registerCommand(new DebugCommand());

        try {
            SpecialHypixelRank.createSpecialHypixelRanks();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void loadApi() {
        HypixelHttpClient client = new ApacheHttpClient(UUID.fromString(API_KEY));
        API = new HypixelAPI(client);
    }

    public static void shutdownApi() {
        API.shutdown();
        API = null;
    }
}
