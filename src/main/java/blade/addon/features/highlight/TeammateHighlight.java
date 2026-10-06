package blade.addon.features.highlight;

import blade.addon.features.dungeon.HidePlayers;
import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TeammateHighlight {

    private static final ConcurrentLinkedQueue<Player> teammates = new ConcurrentLinkedQueue<>();

    public static void init() {

        Events.ON_ENTITY_TRACKED.register((entity, world) -> {
            if (!Location.inDungeon()) return false;
            if (entity instanceof Player player) {
                if (EntityUtil.isARealPlayer(player) && !EntityUtil.isClientPlayer(player) && !teammates.contains(player)) {
                    teammates.add(player);
                }
            }
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(location -> {
            teammates.clear();
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> teammates.removeIf(Entity::isRemoved));

        LevelRenderEvents.COLLECT_SUBMITS.register(TeammateHighlight::renderOutline);
        LevelRenderEvents.COLLECT_SUBMITS.register(TeammateHighlight::renderText);
    }

    private static boolean shouldRender(Player player) {
        if (Dungeons.dontHighlightHiddenTeammates && HidePlayers.shouldHidePlayers(player)) return true;
        return Dungeons.dontHighlightVisibleTeammates && !HidePlayers.shouldHidePlayers(player);
    }

    private static void renderOutline(LevelRenderContext context) {
        if (!Dungeons.highlightTeammates) return;

        teammates.forEach(player -> {
            if (shouldRender(player)) return;
            DungeonClass clazz = DungeonClass.getClass(player);
            if (clazz == null) return;

            int color = DungeonClass.getColor(clazz);

            float[] rgba = RenderUtils.toFloats(color);
            RenderUtils.drawOutlinedBox(context, EntityUtil.getBox(player), rgba, true);

        });
    }

    private static void renderText(LevelRenderContext context) {
        if (!Dungeons.renderClassName) return;

        teammates.forEach(player -> {
            if (shouldRender(player)) return;
            DungeonClass clazz = DungeonClass.getClass(player);
            if (clazz == null) return;

            int color = DungeonClass.getColor(clazz);

            Component text = Component.literal(player.getName().getString()).withColor(color).append(Component.literal(" [" + DungeonClass.getChar(clazz) + "]").withColor(Constants.YELLOW));
            Vec3 pos = EntityUtil.getLerpedPos(player);
            RenderUtils.renderText(context, text, pos.x(), pos.y() + 2.75, pos.z(), 2);

        });
    }
}
