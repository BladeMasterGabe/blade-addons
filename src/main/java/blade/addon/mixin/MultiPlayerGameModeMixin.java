package blade.addon.mixin;

import blade.addon.features.item.DropAnimation;
import blade.addon.features.item.ProtectItem;
import blade.addon.features.other.pet.SelectedPet;
import blade.addon.utils.config.values.Dungeons;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "handleContainerInput", at=@At("HEAD"))
    public void handleInventoryMouseClick(int containerId, int slotNum, int buttonNum, ContainerInput containerInput, Player player, CallbackInfo ci) {
        SelectedPet.testLoadoutClick(containerId, slotNum, buttonNum, containerInput, player);
    }

    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    public void protectItem(LocalPlayer player, boolean all, CallbackInfo ci) {
        if (Dungeons.dontProtectHeldItem && ProtectItem.isInADungeon()) return;

        if (ProtectItem.protect(player.getInventory().getSelectedItem())) {
            ci.cancel();
        }
    }

    @Inject(method = "dropItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
    public void getDropped(LocalPlayer player, boolean all, CallbackInfo ci, @Local(name = "prediction") ItemStack prediction) {
        if (!DropAnimation.shouldProceed()) return;

        int slot = player.getInventory().getSelectedSlot();
        DropAnimation.setDroppedData(prediction, slot);
    }
}