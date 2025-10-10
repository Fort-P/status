package org.fortp.status.events;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.fortp.status.Status;

import java.util.Iterator;
import java.util.UUID;

public class SleepEvents {
    public static void init() {
        EntitySleepEvents.START_SLEEPING.register((player, sleepingPos) -> {
            if (!Status.noSleepers.isEmpty()) {
                ServerPlayerEntity sPlayer = player.getEntityWorld().getServer().getPlayerManager().getPlayer(player.getUuid());
                sPlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(0, 12000, 0));
                sPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("No Sleep")));
                sPlayer.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal(getSubtext(player.getEntityWorld().getServer()))));
            }
        });

        EntitySleepEvents.STOP_SLEEPING.register((player, sleepingPos) -> {
            ServerPlayerEntity sPlayer = player.getEntityWorld().getServer().getPlayerManager().getPlayer(player.getUuid());
            sPlayer.networkHandler.sendPacket(new ClearTitleS2CPacket(true));
        });
    }

    public static String getSubtext(MinecraftServer server) {
        Iterator<UUID> iterator = Status.noSleepers.iterator();
        UUID first = iterator.next();

        String name = "Unknown";
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(first);
        if (player != null) {
            name = player.getName().getString();
        }

        int others = Status.noSleepers.size() - 1;
        if (others > 0) {
            return name + " +" + others + " do not want you to sleep";
        } else {
            return name + " does not want you to sleep";
        }
    }

}
