package org.fortp.status;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.fortp.status.commands.Commands;
import org.fortp.status.events.ServerEvents;
import org.fortp.status.events.SleepEvents;
import org.fortp.status.utils.PlayerData;
import org.fortp.status.utils.TickScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.UUID;

public class Status implements ModInitializer {
    public static final String ID = "status";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
    public static HashSet<UUID> noSleepers = new HashSet<>();

    @Override
    public void onInitialize() {
        TickScheduler.init();
        ServerEvents.init();
        SleepEvents.init();
        PolymerResourcePackUtils.addModAssets(ID);

        CommandRegistrationCallback.EVENT.register(Commands::registerCommands);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            if (player instanceof PlayerData playerData) {
                playerData.status$setAvailability(1);
                playerData.status$setStatus(4);
                player.setCustomName(Text.literal(playerData.status$getAvailability() + "/" + playerData.status$getStatus())
                        .setStyle(Style.EMPTY.withFont(Identifier.of(Status.ID, "status")))
                        .append(Text.literal(" " + player.getGameProfile().getName())
                                .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))));
            }
        });
    }
}