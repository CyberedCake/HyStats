package com.github.cyberedcake.hystats;

import com.github.cyberedcake.hystats.command.DebugCommand;
import com.github.cyberedcake.hystats.command.StatsCommand;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.UUID;

@Mod(modid = "hystats", useMetadata=true)
public class ExampleMod {

    public static final String API_KEY = "c2e835e1-2a7b-435d-b23b-d1a680e71a3d";
    public static HypixelAPI API;

    static { loadApi(); }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new StatsCommand());
        ClientCommandHandler.instance.registerCommand(new DebugCommand());
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
