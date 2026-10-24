package blade.addon.utils.rendering;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class RenderLayers {
    public static final RenderType FILLED = RenderType.create("filled-layer",
            RenderSetup.builder(RenderPipelines.FILLED).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());

    public static final RenderType FILLED_ESP = RenderType.create("filled-esp-layer",
            RenderSetup.builder(RenderPipelines.FILLED_ESP).createRenderSetup());

    public static final RenderType LINE = RenderType.create("line-layer",
            RenderSetup.builder(RenderPipelines.LINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());

    public static final RenderType LINE_ESP = RenderType.create("line-esp-layer",
            RenderSetup.builder(RenderPipelines.LINE_ESP).createRenderSetup());
}