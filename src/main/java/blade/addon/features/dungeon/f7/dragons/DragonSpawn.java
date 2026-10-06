package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.events.Events;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.sounds.SoundEvents;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DragonSpawn {

    public static final int SPAWN_DURATION = 100;

    private static final ConcurrentLinkedQueue<Dragon> dragons = new ConcurrentLinkedQueue<>();

    public static void init() {
        Events.ON_PARTICLE.register(packet -> {
            if (!validParticle(packet)) return false;
            Dragon dragon = Dragon.getDragon(packet.getX(), packet.getY(), packet.getZ());
            addDragon(dragon);
            return false;
        });

        Events.ON_SERVER_TICK.register(() -> {
            dragons.removeIf(dragon -> {
                dragon.tick--;
                return dragon.tick <= 0;
            });

            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            resetInfo();
            return false;
        });
    }

    private static void addDragon(Dragon dragon) {
        if (dragon == Dragon.NONE || dragons.contains(dragon) || dragon.tick > 0) return;


        dragon.tick = SPAWN_DURATION;
        dragons.add(dragon);

        if (Floor7.sendSoundOnDragSpawn) {
            Misc.sendSound(SoundEvents.NOTE_BLOCK_PLING.value(), 0.75f, 1);
        }
    }


    private static boolean validParticle(ClientboundLevelParticlesPacket packet) {
        if (packet.getCount() != 20) return false;
        if (packet.getY() != 19) return false;
        if (packet.getParticle().getType() != ParticleTypes.FLAME) return false;
        if (packet.getXDist() != 2) return false;
        if (packet.getYDist() != 3) return false;
        if (packet.getZDist() != 2) return false;
        if (packet.getX() % 1 != 0) return false;
        if (packet.getZ() % 1 != 0) return false;
        return true;
    }

    private static void resetInfo() {
        for (Dragon dragon : Dragon.values()) {
            dragon.tick = 0;
        }
    }

    public static Dragon getDragon() {
        Dragon dragon = dragons.peek();
        if (dragon == null) return Dragon.NONE;
        return dragon;
    }
}
