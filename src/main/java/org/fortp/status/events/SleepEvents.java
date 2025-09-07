package org.fortp.status.events;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.fortp.status.Status;

public class SleepEvents {
    public static void init() {
        EntitySleepEvents.START_SLEEPING.register((player, sleepingPos) -> {
            if (!Status.noSleepers.isEmpty()) {
                ServerPlayerEntity sPlayer = player.getServer().getPlayerManager().getPlayer(player.getUuid());
                sPlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(0, 12000, 0));
                sPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("No Sleep")));
            }
        });

        EntitySleepEvents.STOP_SLEEPING.register((player, sleepingPos) -> {
            ServerPlayerEntity sPlayer = player.getServer().getPlayerManager().getPlayer(player.getUuid());
            sPlayer.networkHandler.sendPacket(new ClearTitleS2CPacket(true));
        });
    }
}
