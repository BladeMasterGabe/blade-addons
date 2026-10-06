package blade.addon.utils.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RenderHandler {

    private final RenderType renderType;

    public RenderHandler(RenderType renderType)  {
        this.renderType = renderType;
    }

    private final List<RenderingEvent> listeners = new ArrayList<>();

    public void register(RenderingEvent listener) {
        listeners.add(listener);
    }

    public void invoke(Consumer<RenderingEvent> action) {
        if (listeners.isEmpty()) return;
        for (RenderingEvent listener : listeners) {
            action.accept(listener);
        }
    }

    public void init(LevelRenderContext context) {
        LevelRenderState worldState = context.levelState();
        if (worldState == null) return;
        Vec3 camera = worldState.cameraRenderState.pos;
        PoseStack matrices = context.poseStack();
        if (matrices == null) return;
        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        SubmitNodeCollector collector = context.submitNodeCollector();

        collector.submitCustomGeometry(
                matrices,
                renderType,
                (pose, vertexConsumer) -> {
                    invoke(renderingEvent ->
                            renderingEvent.render(context, matrices, vertexConsumer)
                    );
                }
        );

        matrices.popPose();
    }

}
