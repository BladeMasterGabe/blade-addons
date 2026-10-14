package blade.addon.features.dungeon.blood;

import blade.addon.utils.config.components.Categories;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.dungeon.DungeonClass;
import blade.addon.utils.dungeon.Phase;
import blade.addon.utils.events.Events;
import blade.addon.utils.rendering.RenderUtils;
import config.practical.hud.HUDCategory;
import config.practical.hud.HUDComponent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class BloodCampTimer extends HUDComponent {

    private int tick = 0;

    public BloodCampTimer() {
        super("Blood camp timer");
    }

    @Override
    public void init() {
        Events.ON_SERVER_TICK.register(() -> {
            if (Phase.isCampActive()) tick++;
            return false;
        });

        Events.ON_LOCATION_CHANGE.register(_ -> {
            tick = 0;
            return false;
        });
    }

    @Override
    public int getWidth() {
        return 30;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public boolean editable() {
        return Dungeons.enableBloodCampTimer;
    }

    @Override
    public boolean shouldRender() {
        return Phase.isCampActive() && Dungeons.enableBloodCampTimer && DungeonClass.isClass(DungeonClass.MAGE);
    }

    @Override
    public List<HUDCategory> categories() {
        return List.of(Categories.CLEAR);
    }

    @Override
    public void render(@NonNull GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderUtils.drawTimer(this, guiGraphicsExtractor, tick, 0xffffffff);
    }
}
