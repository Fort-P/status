package org.fortp.status.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.fortp.status.Status;
import org.fortp.status.gui.StatusGui;

public class Commands {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("status")
                        .executes(Commands::status)
        );
        dispatcher.register(
                CommandManager.literal("getNoSleepers")
                        .executes(Commands::getNoSleepers)
        );

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

    private static int getNoSleepers(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        source.sendFeedback(() -> Text.of(Status.noSleepers.stream().toList().toString()), false);
        return 1;
    }
}
