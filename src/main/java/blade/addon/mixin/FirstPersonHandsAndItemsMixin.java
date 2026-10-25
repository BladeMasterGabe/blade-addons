package blade.addon.mixin;

import blade.addon.features.item.DropAnimation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItems.class)
public abstract class FirstPersonHandsAndItemsMixin {
    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private float mainHandHeight;
    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
    private void test(CallbackInfo ci, @Local LocalPlayer clientPlayer, @Local ItemStack itemStack) {
        if (!DropAnimation.shouldProceed()) return;
        if (itemStack.getItem() == Items.AIR) return;
        ItemStack prevDropped = DropAnimation.getPrevDroppedItem();
        int slot = DropAnimation.getSelectedSlot();
        if (prevDropped == null) return;

        //check if item has changed
        if (prevDropped.getItem() != itemStack.getItem()) {
            DropAnimation.clearData();
        }

        //check if slot has changed
        if (slot != clientPlayer.getInventory().getSelectedSlot()) {
            DropAnimation.clearData();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void changeHeld(CallbackInfo ci) {
        if (!DropAnimation.shouldProceed()) return;
        ItemStack stack = DropAnimation.getPrevDroppedItem();
        if (stack == null) return;
        if (stack.getItem() == Items.AIR) return;
        mainHandItem = stack;
        this.mainHandHeight = 1;
    }
}