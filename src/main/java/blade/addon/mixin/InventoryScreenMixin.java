package blade.addon.mixin;

import blade.addon.features.other.InventoryButton;
import blade.addon.features.other.SearchBar;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {
    public InventoryScreenMixin(InventoryMenu handler, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
        super(handler, recipeBook, inventory, title);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At("TAIL"))
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        Matrix3x2fStack stack = graphics.pose();
        int x = this.leftPos;
        int y = this.topPos;
        stack.pushMatrix();
        stack.translate(x, y);
        InventoryButton.renderAll(graphics, mouseX, mouseY, a);
        stack.popMatrix();
        SearchBar.render(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        InventoryButton.parseClicks(click.x() - leftPos, click.y() - topPos);
        return super.mouseClicked(click, doubled);
    }

    @Inject(method = "extractLabels", at = @At("HEAD"), cancellable = true)
    protected void drawForeground(GuiGraphicsExtractor graphics, int xm, int ym, CallbackInfo ci) {
        ci.cancel();
    }
}