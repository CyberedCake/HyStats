package com.github.cyberedcake.hystats;

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

    public static final String API_KEY = "1b80668b-b1db-4bd3-b059-9bbeade5ea60";
    public static final HypixelAPI API;

    static {
        HypixelHttpClient client = new ApacheHttpClient(UUID.fromString(API_KEY));
        API = new HypixelAPI(client);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new StatsCommand());
    }
}
