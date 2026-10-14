package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.Constants;
import blade.addon.utils.Location;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.data.EntityUtil;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DragonHealth {

    static class DataHolder {
        EnderDragon dragon;
        float health;

        public DataHolder(EnderDragon dragon, float health) {
            this.dragon = dragon;
            this.health = health;
        }
    }

    static final ConcurrentLinkedQueue<DataHolder> dragons = new ConcurrentLinkedQueue<>();

    public static void init() {
        Events.ON_ENTITY_SPAWNED.register((entity, _) -> {
            if (!Location.inDungeon()) return false;
            if (entity instanceof EnderDragon dragon) dragons.add(new DataHolder(dragon, dragon.getHealth()));

            return false;
        });

        ClientTickEvents.END_LEVEL_TICK.register(_ -> dragons.removeIf(dataHolder -> dataHolder.dragon.isRemoved()));
        LevelRenderEvents.COLLECT_SUBMITS.register(DragonHealth::render);
        Events.ON_LOCATION_CHANGE.register(_ -> {
            reset();
            return false;
        });
    }

    private static void reset() {
        dragons.clear();
    }

    private static int getColor(float health) {
        if (health >= 7.5e8) return Constants.GREEN;
        if (health >= 5e8) return Constants.YELLOW;
        if (health >= 2.5e8) return Constants.GOLD;
        return Constants.RED;
    }

    private static void render(LevelRenderContext context) {
        if (!Floor7.dragonHealth) return;

        dragons.forEach(dataHolder -> {
            EnderDragon dragon = dataHolder.dragon;
            float currHealth =  dragon.getHealth();

            if (currHealth != 1024.0f) dataHolder.health = currHealth;
            float health = dataHolder.health;

            if (health == 0) return;
            RenderUtils.renderText(context, Component.literal(RenderUtils.formatNumber(health)).withColor(getColor(health)), EntityUtil.getLerpedPos(dragon), 5);
        });
    }
}