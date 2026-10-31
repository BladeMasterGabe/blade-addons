package blade.addon.features.dungeon.f7.dragons;

import blade.addon.utils.Misc;
import blade.addon.utils.Scheduler;
import blade.addon.utils.config.values.Floor7;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.events.Events;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.sounds.SoundEvents;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DragonSpawn {
    public static final int SPAWN_DURATION = 100;
    private static Dragon firstDragon = null;
    private static Dragon secondDragon = null;

    private static final ConcurrentLinkedQueue<Dragon> dragons = new ConcurrentLinkedQueue<>();

    public static void init() {
        Events.ON_PARTICLE.register(packet -> {
            if (!validParticle(packet)) return false;
            Dragon dragon = Dragon.getDragon(packet.x(), packet.y(), packet.z());
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

        if (firstDragon == null) firstDragon = dragon;
        else if (secondDragon == null && dragon != firstDragon) secondDragon = dragon;

        if (Floor7.sendSoundOnDragSpawn) {
            Misc.sendSound(SoundEvents.NOTE_BLOCK_PLING.value(), 0.75f, 1);
        }
    }

    private static boolean validParticle(ClientboundLevelParticlesPacket packet) {
        if (packet.count() != 20) return false;
        if (packet.y() != 19) return false;
        if (packet.particle().getType() != ParticleTypes.FLAME) return false;
        if (packet.xDist() != 2) return false;
        if (packet.yDist() != 3) return false;
        if (packet.zDist() != 2) return false;
        if (packet.x() % 1 != 0) return false;
        return packet.z() % 1 == 0;
    }

    private static void resetInfo() {
        for (Dragon dragon : Dragon.values()) {
            dragon.tick = 0;
        }
        secondDragon = null;
        firstDragon = null;
    }

    public static Dragon getDragon() {
        if (Floor7.ignoreFirstDragon && DungeonClass.isClass(DungeonClass.HEALER)) {
            if (firstDragon == null || secondDragon == null) return Dragon.NONE;
            if (dragons.size() > 1) return secondDragon;
        }

        Dragon dragon = dragons.peek();
        if (dragon == null) return Dragon.NONE;
        return dragon;
    }

    public static void debug() {
        addDragon(Dragon.RED);
        Scheduler.scheduleTask(() -> addDragon(Dragon.BLUE), 60);
        Scheduler.scheduleTask(() -> addDragon(Dragon.GREEN), 60 + 120*1);
        Scheduler.scheduleTask(() -> addDragon(Dragon.ORANGE), 60 + 120*2);
    }
}