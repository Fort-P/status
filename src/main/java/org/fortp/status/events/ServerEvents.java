package org.fortp.status.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.fortp.status.Status;
import org.fortp.status.utils.PlayerData;

public class ServerEvents {
    private static int lastTime = -1; // -1 means "not initialized yet"

    public static void init() {
            ServerTickEvents.END_SERVER_TICK.register(ServerEvents::onTick);
    }

    private static void onTick(MinecraftServer server) {
        int time = (int) (server.getOverworld().getTimeOfDay() % 24000);

        if (lastTime != -1) {
            if (lastTime < 12000 && time >= 12000) nightEvent(server);
            if (lastTime > time) {
                dayEvent(server);
                if (time >= 12000) nightEvent(server);
            }
        }

        lastTime = time;
    }


    private static void nightEvent(MinecraftServer server) {
        if (!Status.noSleepers.isEmpty()) {
            server.getPlayerManager().broadcast(Text.literal("Somebody has requested no sleeping tonight")
                    .setStyle(Style.EMPTY.withColor(Formatting.DARK_AQUA)), false);
        }
    }

    private static void dayEvent(MinecraftServer server) {
        Status.noSleepers.clear();
        for (ServerPlayerEntity player : server.getOverworld().getPlayers()) {
            if (player instanceof PlayerData playerData && playerData.status$getNoSleep()) {
                playerData.status$setNoSleep(false);
            }
        }
    }
}
