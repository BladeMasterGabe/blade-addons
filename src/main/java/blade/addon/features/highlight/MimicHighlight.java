package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MimicHighlight {
    private static final AABB box = new AABB(0.0625, 0, 0.0625, 0.9375, 0.875, 0.9375);

    private static final ConcurrentLinkedQueue<TrappedChestBlockEntity> mimics = new ConcurrentLinkedQueue<>();

    public static void init() {
        Events.ON_LOCATION_CHANGE.register(_ -> {
            mimics.clear();
            return false;
        });

       Events.ON_BLOCK_ENTITY.register(blockEntity -> {
          if (!Location.inDungeon()) return false;
          if (blockEntity instanceof TrappedChestBlockEntity mimic && !mimics.contains(mimic)) {
              mimics.add(mimic);
          }
           return false;
       });

       ClientTickEvents.END_CLIENT_TICK.register(_ -> mimics.removeIf(BlockEntity::isRemoved));

        LevelRenderEvents.COLLECT_SUBMITS.register(MimicHighlight::renderFilled);
        LevelRenderEvents.COLLECT_SUBMITS.register(MimicHighlight::renderOutline);
    }

    private static void renderFilled(LevelRenderContext context) {
        if (!MobHighlight.highlightMimicChests || MobHighlight.renderFilled()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.mimicFilledColor);
        mimics.forEach(mimic -> RenderUtils.drawFilledBox(context, box.move(mimic.getBlockPos()), rgba));
    }

    private static void renderOutline(LevelRenderContext context) {
        if (!MobHighlight.highlightMimicChests || MobHighlight.renderOutline()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.mimicOutlineColor);
        mimics.forEach(mimic -> RenderUtils.drawOutlinedBox(context, box.move(mimic.getBlockPos()), rgba));
    }
}