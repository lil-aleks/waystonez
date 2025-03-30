package de.lilaleks.waystonez.gui.waystone.menu;

import de.lilaleks.waystonez.Waystonez;
import de.lilaleks.waystonez.gui.waystone.WaystoneMenu;
import de.lilaleks.waystonez.model.Waystone;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class NameInputMenu extends WaystoneMenu
{
    private final Block block;
    private final JavaPlugin plugin;

    public NameInputMenu(Block block, JavaPlugin plugin)
    {
        super(9);
        this.block = block;
        this.plugin = plugin;
    }

    @Override
    public void onSetItems()
    {
        final ItemStack nameItem = new ItemStack(Material.PAPER);
        final ItemMeta itemMeta = nameItem.getItemMeta();
        itemMeta.displayName(Component.text("Rename Waypoint"));
        nameItem.setItemMeta(itemMeta);
        setItem(5, nameItem, player ->
        {
            player.closeInventory();
            player.sendMessage(Component.text("Type the name for your waystone in chat:"));
            Bukkit.getPluginManager().registerEvents(new Listener()
            {
                @EventHandler
                public void onChat(AsyncPlayerChatEvent event)
                {
                    if (event.getPlayer() != player) return;
                    event.setCancelled(true);
                    String name = event.getMessage();
                    Waystonez.databaseManager.saveWaystone(new Waystone(name, block.getLocation(), player.getUniqueId()));
                    HandlerList.unregisterAll(this);
                }
            }, plugin);
        });
    }
}
