package de.lilaleks.waystonez.event;

import de.lilaleks.waystonez.custom.CustomItemHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;

public class CustomItemEventHandler implements Listener
{
    public final CustomItemHandler[] customItemHandlers;

    public CustomItemEventHandler(JavaPlugin plugin, CustomItemHandler... handler)
    {
        handler = Arrays.stream(handler)
            .filter(Objects::nonNull)      // filtert alle null raus
            .toArray(CustomItemHandler[]::new);

        customItemHandlers = handler;
        for (CustomItemHandler item : customItemHandlers)
        {
            if (item.getRecipe(plugin) != null)
                plugin.getServer().addRecipe(item.getRecipe(plugin));
            if (item instanceof Listener listener)
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent event)
    {
        if (!event.hasItem())
            return;
        if (!event.getItem().hasItemMeta())
            return;

        Arrays.stream(customItemHandlers)
                .filter(customItemHandler -> areItemsSimilarIgnoreDurability(event.getItem(), customItemHandler.getItemStack()))
                .forEach(customItemHandler ->
                {
                    if (event.getAction().isRightClick())
                        customItemHandler.onRightClick(event);
                    if (event.getAction().isLeftClick())
                        customItemHandler.onLeftClick(event);
                });
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        if (!event.getItemInHand().hasItemMeta())
            return;

        Arrays.stream(customItemHandlers)
                .filter(customItemHandler -> areItemsSimilarIgnoreDurability(event.getItemInHand(), customItemHandler.getItemStack()))
                .forEach(customItemHandler ->
                {
                    customItemHandler.onPlace(event);
                });
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        Arrays.stream(customItemHandlers)
                .filter(customItemHandler -> customItemHandler.isCustomBlock(event.getBlock()))
                .forEach(customItemHandler ->
                {
                    customItemHandler.onBreak(event);
                });
    }


    private boolean areItemsSimilarIgnoreDurability(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null)
            return false;
        if (!item1.getType().equals(item2.getType()))
            return false;

        return item1.getItemMeta().getCustomModelDataComponent().getStrings().equals(item2.getItemMeta().getCustomModelDataComponent().getStrings());
    }
}
