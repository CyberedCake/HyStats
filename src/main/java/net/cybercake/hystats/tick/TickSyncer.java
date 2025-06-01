package net.cybercake.hystats.tick;

import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TickSyncer {

    private final ConcurrentLinkedQueue<Runnable> runNextTick;
    private TickCapturer capturerEvent;

    public TickSyncer() {
        this.runNextTick = new ConcurrentLinkedQueue<>();
        this.capturerEvent = null;
    }

    public void sync(Runnable runnable) {
        this.runNextTick.add(runnable); // enqueue

        if (this.capturerEvent == null) {
            this.capturerEvent = new TickCapturer(this);
            MinecraftForge.EVENT_BUS.register(this.capturerEvent);
        }
    }

    void clearTick() {
        this.runNextTick.clear();

        if (this.capturerEvent != null) {
            MinecraftForge.EVENT_BUS.unregister(this.capturerEvent);
            this.capturerEvent = null;
        }
    }

    public ConcurrentLinkedQueue<Runnable> queue() {
        return this.runNextTick;
    }

}
