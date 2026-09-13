package blade.addon.utils.events.interfaces;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;

public interface SlotClickEvent {
    boolean onSlot(Slot slot, int slotId, int button, ContainerInput containerInput, Screen container);
}
