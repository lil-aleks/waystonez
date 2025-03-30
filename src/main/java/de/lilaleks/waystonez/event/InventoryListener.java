package de.lilaleks.waystonez.event;

import de.lilaleks.waystonez.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener
{
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        if (!(inventory.getHolder() instanceof Menu menu)) return;
        event.setCancelled(true);
        menu.click((Player) event.getWhoClicked(), event.getSlot());
    }
}
