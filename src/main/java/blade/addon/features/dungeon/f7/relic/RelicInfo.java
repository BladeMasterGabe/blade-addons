package blade.addon.features.dungeon.f7.relic;

import blade.addon.utils.Misc;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;

public class RelicInfo {

    static boolean spawned = false;
    static boolean trackSound = false;
    static boolean trackHealth = false;

    static int tickSound = 0;
    static int tickHealth = 0;

    public static void init() {

        Events.ON_WORLD_CHANGE.register(() -> {
            spawned = false;
            tickSound = 0;
            tickHealth = 0;
            return false;
        });

        ClientTickEvents.END_LEVEL_TICK.register(level -> {
            if (spawned || !inValidPhase()) return;

            level.entitiesForRendering().forEach(entity -> {
                if (spawned) return;
                if (entity instanceof ArmorStand armorStand) {
                    Component custom = armorStand.getItemBySlot(EquipmentSlot.HEAD).getCustomName();
                    if (custom != null && custom.getString().contains("Relic")) {
                        Misc.addChatMessage(Component.literal("ticks since (Sound) for relic: " + tickSound));
                        Misc.addChatMessage(Component.literal("ticks since (health) for relic: " + tickHealth));
                        spawned = true;
                    }
                }
            });
        });




        Events.ON_SOUND.register((soundEvent, volume, pitch) -> {
            if (spawned || !inValidPhase()) return false;
            if (soundEvent == SoundEvents.WITHER_HURT && volume == 15.0f && pitch == 1.0f) {
                trackSound = true;
            }
            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            if (spawned) return false;
            if (trackSound) tickSound++;
            if (trackHealth) tickHealth++;
            return false;
        });
    }

    public static boolean inValidPhase() {
        return Phase.inP4() || Phase.inP5();
    }

    public static void startBar() {
        trackHealth = true;
    }

}
