package org.fortp.status.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.fortp.status.gui.StatusGui;

public class Commands {
    public static LiteralArgumentBuilder<ServerCommandSource> registerCommands() {
        return CommandManager.literal("status")
                .executes(Commands::status);
    }

    private static int status(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            new StatusGui(player);
            return 1;
        } else {
            return 0;
        }
    }
}
