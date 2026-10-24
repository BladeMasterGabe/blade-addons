package blade.addon.mixin;

import blade.addon.utils.data.EntityUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z", at = @At("TAIL"))
    public void swingHand(InteractionHand hand, SwingAnimation animation, boolean sendToSwingingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (EntityUtil.isClientPlayer((Entity) (Object) this)) {
            blade.addon.features.other.SwingAnimation.consumeSwing();
        }
    }
}