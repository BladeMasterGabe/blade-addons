package blade.addon.mixin;

import blade.addon.features.other.SwingAnimation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class HeldItemRendererMixin {
    @Inject(method = "applyItemArmTransform", at = @At("HEAD"), cancellable = true)
    private void applyEquipOffset(PoseStack poseStack, HumanoidArm arm, float inverseArmHeight, CallbackInfo ci) {
        if (!SwingAnimation.shouldIgnore()) return;
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate((float) side * 0.56F, -0.52F, -0.72F);
        ci.cancel();
    }

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void swingArm(float animation, PoseStack poseStack, int invert, HumanoidArm arm, CallbackInfo ci) {
        if (!SwingAnimation.shouldIgnore()) return;
        float f = -0.4F * Mth.sin(Mth.sqrt(1) * (float) Math.PI);
        float g = 0.2F * Mth.sin(Mth.sqrt(1) * (float) (Math.PI * 2));
        float h = -0.2F * Mth.sin(1 * (float) Math.PI);
        poseStack.translate(invert * f, g, h);
        ci.cancel();
    }
}