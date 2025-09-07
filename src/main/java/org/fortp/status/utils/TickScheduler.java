package org.fortp.status.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.LinkedList;
import java.util.Queue;

public class TickScheduler {
    public static class ScheduledTask {
        public int ticksRemaining;
        Runnable task;

        ScheduledTask(int delay, Runnable task) {
            this.ticksRemaining = delay;
            this.task = task;
        }
    }

    public static final Queue<ScheduledTask> tasks = new LinkedList<>();

    public static void schedule(Runnable task, int delayTicks) {
        tasks.add(new ScheduledTask(delayTicks, task));
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(TickScheduler::tick);
    }

    private static void tick(MinecraftServer server) {
        int size = tasks.size();
        for (int i = 0; i < size; i++) {
            ScheduledTask scheduled = tasks.poll();
            if (scheduled == null) continue;

            if (--scheduled.ticksRemaining <= 0) {
                scheduled.task.run();
            } else {
                tasks.add(scheduled);
            }
        }
    }
}
