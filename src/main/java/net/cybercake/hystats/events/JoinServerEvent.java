package net.cybercake.hystats.events;

import net.cybercake.hystats.HyStats;
import net.cybercake.hystats.utils.UChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinServerEvent {

    public static final Runnable WELCOME_MESSAGE = () -> {
        try {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            System.out.println("Synced! Running for " + player);
            if (player == null) {
                return;
            }
            System.out.println("Detected: " + player.getName());

            UChat.send(UChat.format(
                    String.join("\n", new String[]{
                            "&6&lWelcome to HyStats!",
                            " ",
                            "&eHyStats &fis a mod that allows you to &acheck players' stats &fin-game using &b/stats <player> [<game>]",
                            "&fFor example, &d/stats " + player.getName() + " bedwars &f(try it!)",
                            " ",
                            "&7&oHyStats is not endorsed or officially affiliated with Hypixel.",
                            "&7&oRead Hypixel's server rules and use at your own risk!",
                    }),
                    null, true));
            Minecraft.getMinecraft().getSoundHandler().playSound(
                    PositionedSoundRecord.create(new ResourceLocation("random.levelup"), 1.0F)
            );
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    };
    public static final int WELCOME_DELAY_MS = 5 * 1_000; // 5 seconds

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayer))
            return;

        if (!HyStats.firstRun)
            return;

        enqueueWelcomeMessage();
        HyStats.firstRun = false;
    }

    public static void enqueueWelcomeMessage() {
        System.out.println("Creating thread");
        Thread thread = new Thread(() -> {
            try {
                System.out.println("Delaying...");
                Thread.sleep(WELCOME_DELAY_MS);
                System.out.println("Delay complete, syncing!");
                HyStats.tick.sync(WELCOME_MESSAGE);
            } catch (InterruptedException exception) {
                exception.printStackTrace(System.err);
            }
        }, "HyStats-WelcomeThread");
        thread.start();
    }

}
