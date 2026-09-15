package blade.addon.features.dungeon;

import blade.addon.utils.Location;
import blade.addon.utils.Misc;
import blade.addon.utils.config.values.Dungeons;
import blade.addon.utils.events.Events;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RerollBlocker {

    private static final String[] BLOCKED_REROLLS = {"Recombobulator ", "Wither Shield", "Implosion", "Shadow Warp", "Necron's Handle", "Star", "Dark Claymore", "Dye", "Giant's Sword", "Shadow Fury"};

    private static boolean blockClick = false;
    private static int containerId = Integer.MIN_VALUE;

    public static void init() {

        Events.ON_SCREEN.register(_ -> {
            containerId = Integer.MIN_VALUE;
            return false;
        });

        Events.ON_SLOT_CLICKED.register((slot, slotId, _, containerInput, screen) -> {
            if (!Dungeons.blockExpensiveRerolls || !Location.in(Location.DUNGEON_HUB)) return false;
            if (containerInput == ContainerInput.THROW) return false;
            if (slotId != 50 || slot.getItem().getItem() != Items.FEATHER) return false;

            if (screen instanceof ContainerScreen containerScreen) {
                ChestMenu menu = containerScreen.getMenu();
                if (menu == null) return false;
                Container container = menu.getContainer();
                if (container == null) return false;

                if (menu.containerId != containerId) {
                    scanContainer(container);
                    containerId = menu.containerId;
                }
            }

            ItemStack itemStack = slot.getItem();
            Component customName = itemStack.getCustomName();
            if (customName == null) return false;
            if (!customName.getString().contains("Reroll Chest")) return false;

            return blockClick;
        });

    }

    private static void scanContainer(Container container) {
        for (int i = 0; i < 54; i++) {
           ItemStack itemStack = container.getItem(i);
           if (scanItem(itemStack)) {
               blockClick = true;
               return;
           }
        }

        blockClick = false;
    }

    private static boolean scanItem(ItemStack itemStack) {
        Component component = itemStack.getCustomName();
        if (component == null) return false;

        String blocked = getBlockedItem(component.getString());
        if (blocked != null) {
            Misc.addChatMessage(Component.literal("Blocked reroll {name: " + blocked + "} {Item name: " + component.getString() + "}"));
            return true;
        }

        return false;
    }

    private static String getBlockedItem(String itemName) {
        for (String name : BLOCKED_REROLLS) {
            if (itemName.contains(name)) return name;
        }
        return null;
    }

}
