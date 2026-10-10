package blade.addon.features.highlight;

import blade.addon.utils.Location;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.world.entity.ambient.Bat;

import java.util.concurrent.ConcurrentLinkedQueue;

public class BatHighlight {
    private static final float[] BAT_HEALTHS = {100.0f, 200.0f, 400.0f, 800.0f};

    private static final ConcurrentLinkedQueue<Bat> bats = new ConcurrentLinkedQueue<>();

    public static void init() {
        Events.ON_ENTITY_TRACKED.register((entity, _) -> {
            if (!Location.inDungeon() || !MobHighlight.mobHighlight) return false;
            if (entity instanceof Bat bat && !bats.contains(bat)) {
                for (float health : BAT_HEALTHS) {
                    if (health == bat.getHealth()) {
                        bats.add(bat);
                    }
                }
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            bats.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(_ -> bats.removeIf(bat -> bat.isRemoved() || bat.isDeadOrDying()));

        LevelRenderEvents.COLLECT_SUBMITS.register(BatHighlight::renderFilled);
        LevelRenderEvents.COLLECT_SUBMITS.register(BatHighlight::renderOutline);
    }

    private static void renderFilled(LevelRenderContext context) {
        if (!MobHighlight.mobHighlight || MobHighlight.renderFilled()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.batFilledColor);
        bats.forEach(entity -> RenderUtils.drawFilledBox(context, EntityUtil.getBox(entity), rgba));
    }

    private static void renderOutline(LevelRenderContext context) {
        if (!MobHighlight.mobHighlight || MobHighlight.renderOutline()) return;

        float[] rgba = RenderUtils.toFloats(MobHighlight.batOutlineColor);
        bats.forEach(entity -> RenderUtils.drawOutlinedBox(context, EntityUtil.getBox(entity), rgba));
    }
}