package de.lilaleks.waystonez.event;

import de.lilaleks.waystonez.block.CustomItemHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class CustomItemEventHandler implements Listener
{
    public final CustomItemHandler[] customItemHandlers;

    public CustomItemEventHandler(JavaPlugin plugin, CustomItemHandler... handler)
    {
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


    private boolean areItemsSimilarIgnoreDurability(ItemStack item1, ItemStack item2)
    {
        if (item1 == null || item2 == null)
            return false;
        if (!item1.getType().equals(item2.getType()))
            return false;
        if (!(item1.getItemMeta() instanceof Damageable))
            return false;
        Damageable meta1 = (Damageable) item1.getItemMeta();
        meta1.setDamage(0);

        Damageable meta2 = (Damageable) item2.getItemMeta();
        meta2.setDamage(0);
        item1.setItemMeta(meta1);
        item2.setItemMeta(meta2);
        return item1.isSimilar(item2);
    }
}
