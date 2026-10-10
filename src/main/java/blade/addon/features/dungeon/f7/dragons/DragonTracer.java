package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.rendering.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;

public class DragonTracer {

    public static void init() {
        LevelRenderEvents.COLLECT_SUBMITS.register(DragonTracer::render);
    }

    private static void render(LevelRenderContext context) {
        if (!Floor7.dragonTracer) return;

        Dragon dragon = DragonSpawn.getDragon();
        if (dragon == null || dragon == Dragon.NONE) return;

        RenderUtils.renderLineTo(context, dragon.spawnPos, dragon.color);
    }
}