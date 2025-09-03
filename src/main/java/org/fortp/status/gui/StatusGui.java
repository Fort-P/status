package org.fortp.status.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.fortp.status.Status;
import org.fortp.status.utils.PlayerData;

public class StatusGui extends SimpleGui {

    public StatusGui(ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X6, player, true);

        if (player instanceof PlayerData playerData) {
            customizeTitle(playerData.status$getAvailability(), playerData.status$getStatus());
        }
        setupSlots();

        this.open();
    }

    private void customizeTitle(int availability, int status) {
        char background = (char) (0xE000 | (availability << 4) | status);
        this.setTitle(Text.literal("." + background).setStyle(Style.EMPTY.withFont(Identifier.of(Status.ID, "status")).withColor(Formatting.WHITE)));
        this.setSlot(45, new GuiElementBuilder(Identifier.of(Status.ID, "titlehider")).hideTooltip());
    
        // Hide the Inventory text
        this.setSlot(45, new GuiElementBuilder(Identifier.of(Status.ID, "titlehider")).hideTooltip());
    }

    private void setupSlots() {
        for (int slot : new int[]{12, 13, 14, 15, 16}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> availabilityHandler(1)));
        }
        for (int slot : new int[]{30, 31, 32, 33, 34}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> availabilityHandler(2)));
        }
        for (int slot : new int[]{48, 49, 50, 51, 52}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> availabilityHandler(3)));
        }

        for (int slot : new int[]{55, 56, 57}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> statusHandler(4)));
        }
        for (int slot : new int[]{59, 60, 61}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> statusHandler(5)));
        }
        for (int slot : new int[]{75, 76, 77, 78}) {
            this.setSlot(slot, new GuiElementBuilder(Identifier.of(Status.ID, "invisible"))
                    .hideTooltip()
                    .setCallback(() -> statusHandler(1)));
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
            customizeTitle(playerData.status$getAvailability(), playerData.status$getStatus());

            // UI_BUTTON_CLICK is the only sound that isn't a sound event and instead a reference
            player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                    SoundEvents.UI_BUTTON_CLICK,
                    SoundCategory.UI,
                    player.getX(), player.getY(), player.getZ(),
                    1.0F, 1.0F, 0L
            ));
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
            customizeTitle(playerData.status$getAvailability(), playerData.status$getStatus());

            // UI_BUTTON_CLICK is the only sound that isn't a sound event and instead a reference
            player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                    SoundEvents.UI_BUTTON_CLICK,
                    SoundCategory.UI,
                    player.getX(), player.getY(), player.getZ(),
                    1.0F, 1.0F, 0L
            ));
        }
    }

    @Override
    public void onClose() {
        PlayerManager playerManager = player.getServer().getPlayerManager();
        playerManager.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
        super.onClose();
    }
}