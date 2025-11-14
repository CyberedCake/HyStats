package net.cybercake.hystats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.cybercake.hystats.commands.DeveloperCommand;
import net.cybercake.hystats.commands.stats.StatsCommandManager;
import net.cybercake.hystats.api.ApiManager;
import net.cybercake.hystats.events.JoinServerEvent;
import net.cybercake.hystats.exceptions.ExceptionManager;
import net.cybercake.hystats.hypixel.ranks.SpecialHypixelRank;
import net.cybercake.hystats.tick.TickSyncer;
import net.cybercake.hystats.utils.VersionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.*;
import java.net.SocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mod(modid = "hystats", useMetadata=true)
public class HyStats {

    public static final String VERSION = "1.0.0";

    public static ApiManager hypixel;
    public static StatsCommandManager command;
    public static ExceptionManager exceptions;
    public static TickSyncer tick;

    public static boolean firstRun;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = event.getSuggestedConfigurationFile().getParentFile();
        file = new File(new File(file, "hystats"), "version.json");

        if (!file.exists()) {
            System.out.println("First launch of HyStats detected (v" + VERSION + " -> " + file.getAbsoluteFile() + ")");
            firstRun = true;
            VersionData data = new VersionData().version(VERSION);
            try {
                file.getParentFile().mkdirs();
                FileWriter writer = new FileWriter(file);
                gson.toJson(data, writer);
                writer.close();
            } catch (IOException exception) {
                exception.printStackTrace(System.err);
            }
            return;
        }

        try {
            FileReader reader = new FileReader(file);
            VersionData data = gson.fromJson(reader, VersionData.class);
            reader.close();

            data.version(VERSION);

            FileWriter writer = new FileWriter(file);
            gson.toJson(data, writer);
            writer.close();
         } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("HyStats version " + VERSION);
        if (firstRun) {
            System.out.println("(first launch)");
        }

        command = new StatsCommandManager();
        ClientCommandHandler.instance.registerCommand(command);
        ClientCommandHandler.instance.registerCommand(new DeveloperCommand());

        exceptions = new ExceptionManager();

        MinecraftForge.EVENT_BUS.register(new JoinServerEvent());

        hypixel = new ApiManager();
        hypixel.reloadApi();

        tick = new TickSyncer();

        try {
            SpecialHypixelRank.createSpecialHypixelRanks();
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    public static SocketAddress getConnectedServer() {
        try {
            return Minecraft.getMinecraft().getNetHandler().getNetworkManager().getRemoteAddress();
        } catch (Exception exception) {
            return null;
        }
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
