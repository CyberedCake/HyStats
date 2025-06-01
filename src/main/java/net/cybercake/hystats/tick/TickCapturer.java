package net.cybercake.hystats.tick;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TickCapturer {

    private final TickSyncer syncer;
    private final long created;

    public TickCapturer(TickSyncer syncer) {
        this.syncer = syncer;
        this.created = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        if (System.currentTimeMillis() - created >= 10 * 1_000) {
            // clearly something isn't right if this event is active for more than ten seconds, so it will self-destruct
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }

        ConcurrentLinkedQueue<Runnable> tasks = this.syncer.queue();
        Runnable runnable;
        while ((runnable = tasks.poll()) != null) {
            runnable.run();
        }

        this.syncer.clearTick();
    }

}
