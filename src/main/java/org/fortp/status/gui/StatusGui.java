package org.fortp.status.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.fortp.status.Status;
import org.fortp.status.utils.PlayerData;
import org.fortp.status.utils.TickScheduler;

import java.util.HashSet;
import java.util.UUID;

public class StatusGui extends SimpleGui {
    private final HashSet<UUID> noSleepersOld;

    public StatusGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, true);

        if (player instanceof PlayerData playerData) {
            customizeTitle(playerData.status$getAvailability(), playerData.status$getStatus(), playerData.status$getNoSleep());
        }
        setupSlots();


        this.noSleepersOld = new HashSet<>(Status.noSleepers);

        this.open();
    }

    private void customizeTitle(int availability, int status, boolean noSleep) {
        char[] statuses = {
                '\uE010', '\uE020', '\uE030',
                '\uE040', '\uE050', '\uE060'
        };

        char[] buttons = new char[statuses.length];

        for (int i = 0; i < statuses.length; i++) {
            int button = statuses[i];

            // extract the second-from-right hex digit
            int secondFromRight = (button >> 4) & 0xF;

            // if availability or status match this digit, set last nibble = 1
            if (availability == secondFromRight || status == secondFromRight) {
                button = (button & 0xFFF0) | 0x1;
            }

            buttons[i] = (char) button;
        }

        StringBuilder title = new StringBuilder();
        title.append(".\uE000-");
        if (noSleep) {
            title.append("\uE001 \uE004_");
        } else {
            title.append("\uE002 \uE003_");
        }
        for (char button : buttons) {
            title.append(button);
            title.append("_");
        }

        this.setTitle(Text.literal(title.toString()).setStyle(Style.EMPTY.withFont(Identifier.of(Status.ID, "status")).withColor(Formatting.WHITE)));
    
        // Hide the Inventory text
        this.setSlot(45, new GuiElementBuilder(Identifier.of(Status.ID, "titlehider")).hideTooltip());
    }

    private void setupSlots() {
        this.setSlot(10, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                .hideTooltip()
                .setCallback(() -> sleepHandler(false)));
        for (int slot : new int[]{12, 13, 14, 15, 16}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> sleepHandler(true)));
        }
        for (int slot : new int[]{30, 31, 32, 33, 34}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> availabilityHandler(1)));
        }
        for (int slot : new int[]{39, 40, 41, 42, 43}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> availabilityHandler(2)));
        }
        for (int slot : new int[]{48, 49, 50, 51, 52}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> availabilityHandler(3)));
        }

        for (int slot : new int[]{57, 58, 59, 60, 61}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> statusHandler(4)));
        }
        for (int slot : new int[]{66, 67, 68, 69, 70}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> statusHandler(5)));
        }
        for (int slot : new int[]{75, 76, 77, 78, 79}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> statusHandler(6)));
        }
    }

    private void sleepHandler(boolean noSleep) {
        ServerPlayerEntity player = this.getPlayer();
        if (player instanceof PlayerData playerData) {
            if (noSleep) {
                playerData.status$setNoSleep(noSleep);
            } else {
                playerData.status$setNoSleep(noSleep);
                Status.noSleepers.remove(player.getUuid());
            }
            playerData.status$setNoSleep(noSleep);
            customizeTitle(playerData.status$getAvailability(), playerData.status$getStatus(), playerData.status$getNoSleep());
        }
    }

    private void availabilityHandler(int newAvailability) {
        ServerPlayerEntity player = this.getPlayer();
        if (player instanceof PlayerData playerData) {
            playerData.status$setAvailability(newAvailability);
            player.setCustomName(Text.literal(playerData.status$getAvailability() + "/" + playerData.status$getStatus())
                    .setStyle(Style.EMPTY.withFont(Identifier.of(Status.ID, "status")))
                    .append(Text.literal(" " + player.getGameProfile().getName())
                            .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))));
            customizeTitle(playerData.status$getAvailability(), playerData.status$getStatus(), playerData.status$getNoSleep());

            player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
        }
    }

    private void statusHandler(int newStatus) {
        ServerPlayerEntity player = this.getPlayer();
        if (player instanceof PlayerData playerData) {
            playerData.status$setStatus(newStatus);
            player.setCustomName(Text.literal(playerData.status$getAvailability() + "/" + playerData.status$getStatus())
                    .setStyle(Style.EMPTY.withFont(Identifier.of(Status.ID, "status")))
                    .append(Text.literal(" " + player.getGameProfile().getName())
                            .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))));
            customizeTitle(playerData.status$getAvailability(), playerData.status$getStatus(), playerData.status$getNoSleep());

            player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
        }
    }

    @Override
    public void onClose() {
        PlayerManager playerManager = player.getServer().getPlayerManager();
        playerManager.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));

        if (player instanceof PlayerData playerData && playerData.status$getNoSleep() && !Status.noSleepers.contains(player.getUuid())) {
            if (player.getServer().getOverworld().getTimeOfDay() %  24000 > 12000) {
                playerData.status$setNoSleep(false);
                player.sendMessage(Text.literal("It's too late to request no sleeping tonight")
                        .setStyle(Style.EMPTY.withColor(Formatting.DARK_AQUA)), false);
            } else {
                Status.noSleepers.add(player.getUuid());
            }
        }

        if (Status.noSleepers.isEmpty() && !this.noSleepersOld.isEmpty()) {
            player.getServer().getPlayerManager().broadcast(Text.literal("All players are now good to sleep!")
                    .setStyle(Style.EMPTY.withColor(Formatting.DARK_AQUA)), false);
        }

        // Have to use an array to get around self-reference
        Runnable[] task = new Runnable[1];
        task[0] = () -> {
            if (player instanceof PlayerData playerData && playerData.status$getNoSleep()) {
                player.sendMessage(Text.literal("You have no sleeping active"), true);

                // reschedule itself to keep the message on screen
                TickScheduler.schedule(task[0], 40);
            }
        };
        TickScheduler.schedule(task[0], 0);

        super.onClose();
    }
}