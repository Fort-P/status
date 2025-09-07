package org.fortp.status.mixins;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.fortp.status.utils.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements PlayerData{
    @Shadow
    public abstract void sendMessage(Text message);

    @Unique private int status$availability = 1;
    @Unique private int status$status = 1;
    @Unique private boolean staus$noSleep = false;

    @Unique
    public void status$setAvailability(int newAvailability) {
        this.status$availability = newAvailability;
    }

    @Unique
    public int status$getAvailability() {
        return this.status$availability;
    }

    @Unique
    public void status$setStatus(int newStatus) {
        this.status$status = newStatus;
    }

    @Unique
    public int status$getStatus() {
        return this.status$status;
    }

    @Override
    public boolean status$getNoSleep() {
        return this.staus$noSleep;
    }

    @Override
    public void status$setNoSleep(boolean noSleep) {
        this.staus$noSleep = noSleep;
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyFromMixin(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof PlayerData playerData) {
            this.sendMessage(Text.literal("hi new creature that shouldn't be new!!!"));
            this.status$availability = playerData.status$getAvailability();
            this.status$status = playerData.status$getStatus();
        }
    }

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    private void getPlayerListNameMixin(CallbackInfoReturnable<Text> cir) {
        // Need to cast 'this' to ServerPlayerEntity to access inherited methods
        ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
        Text currentCustomName = self.getCustomName();

//        if (currentCustomName != null && currentCustomName.getString().startsWith(Status.PREFIX)) {
            cir.setReturnValue(currentCustomName);
//        }
    }
}