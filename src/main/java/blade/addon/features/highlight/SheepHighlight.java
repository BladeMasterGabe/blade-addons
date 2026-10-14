package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.world.entity.animal.sheep.Sheep;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SheepHighlight {
    private static final ConcurrentLinkedQueue<Sheep> sheeps = new ConcurrentLinkedQueue<>();

    public static void init() {
        Events.ON_ENTITY_SPAWNED.register((entity, _) -> {
            if (!Location.inDungeon()) return false;
            if (entity instanceof Sheep sheep && !sheeps.contains(sheep)) {
                sheeps.add(sheep);
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            sheeps.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(_ -> sheeps.removeIf(sheep -> sheep.isRemoved() || sheep.isDeadOrDying()));

        LevelRenderEvents.COLLECT_SUBMITS.register(SheepHighlight::renderFilled);
        LevelRenderEvents.COLLECT_SUBMITS.register(SheepHighlight::renderOutline);
    }

    private static void renderFilled(LevelRenderContext context) {
        if (!MobHighlight.highlightSheep || MobHighlight.renderFilled()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.sheepFilledColor);
        sheeps.forEach(entity -> RenderUtils.drawFilledBox(context, EntityUtil.getBox(entity), rgba));
    }

    private static void renderOutline(LevelRenderContext context) {
        if (!MobHighlight.highlightSheep || MobHighlight.renderOutline()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.sheepFilledColor);
        sheeps.forEach(entity -> RenderUtils.drawOutlinedBox(context, EntityUtil.getBox(entity), rgba));
    }
}