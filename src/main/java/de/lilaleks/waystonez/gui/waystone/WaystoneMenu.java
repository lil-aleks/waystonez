package de.lilaleks.waystonez.gui.waystone;

import de.lilaleks.waystonez.gui.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WaystoneMenu implements Menu
{
    private final Map<Integer, Consumer<Player>> actions = new HashMap<>();
    private final Inventory inventory;

    public WaystoneMenu(int size)
    {
        this.inventory = Bukkit.createInventory(this, size, Component.text("Waystonez"));
    }

    @Override
    public void click(Player player, int slot)
    {
        final Consumer<Player> action = this.actions.get(slot);

        if (action != null)
            action.accept(player);
    }

    @Override
    public void setItem(int slot, ItemStack item)
    {
        setItem(slot, item, player -> {});
    }

    @Override
    public void setItem(int slot, ItemStack item, Consumer<Player> action)
    {
        this.actions.put(slot, action);
        getInventory().setItem(slot, item);
    }

    @Override
    public void onSetItems() {}

    @Override
    public @NotNull Inventory getInventory()
    {
        return inventory;
    }
}
