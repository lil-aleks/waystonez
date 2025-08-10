package de.lilaleks.waystonez.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface Menu extends InventoryHolder
{
    void click(Player player, int slot);
    default void close(InventoryCloseEvent event) {};
    void setItem(int slot, ItemStack item);
    void setItem(int slot, ItemStack item, Consumer<Player> action);
    void onSetItems();

    default void open(Player player) {
        onSetItems();
        player.openInventory(getInventory());
    }
}
