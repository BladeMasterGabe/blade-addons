package blade.addon.utils.rendering;

import blade.addon.utils.Constants;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.resources.Identifier;

public class RenderPipelines {

    private static final DepthStencilState NO_DEPTH = new DepthStencilState(CompareOp.ALWAYS_PASS, false);
    private static final ColorTargetState TRANSLUCENT = new ColorTargetState(BlendFunction.TRANSLUCENT);

    public static final RenderPipeline FILLED = filled("filled-pipe", DepthStencilState.DEFAULT);
    public static final RenderPipeline FILLED_ESP = filled("filled-esp-pipe", NO_DEPTH);
    public static final RenderPipeline LINE = line("line-pipe", DepthStencilState.DEFAULT);
    public static final RenderPipeline LINE_ESP = line("line-esp-pipe", NO_DEPTH);

    private static RenderPipeline filled(String name, DepthStencilState depth) {
        return net.minecraft.client.renderer.RenderPipelines.register(
                RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.DEBUG_FILLED_SNIPPET)
                        .withLocation(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, name))
                        .withDepthStencilState(depth)
                        .withColorTargetState(TRANSLUCENT)
                        .withCull(false)
                        .build());
    }

    private static RenderPipeline line(String name, DepthStencilState depth) {
        return net.minecraft.client.renderer.RenderPipelines.register(
                RenderPipeline.builder(net.minecraft.client.renderer.RenderPipelines.LINES_SNIPPET)
                        .withLocation(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, name))
                        .withDepthStencilState(depth)
                        .withColorTargetState(TRANSLUCENT)
                        .build());
    }
}
