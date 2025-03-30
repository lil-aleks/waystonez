package de.lilaleks.waystonez.gui.waystone.menu;

import de.lilaleks.waystonez.Waystonez;
import de.lilaleks.waystonez.gui.waystone.WaystoneMenu;
import de.lilaleks.waystonez.model.Waystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TeleportMenu extends WaystoneMenu
{
    public final Player player;
    public TeleportMenu(Player player)
    {
        super((Waystonez.databaseManager.getPlayerWaystones(player.getUniqueId()).size() / 9) * 9);
        this.player = player;
    }

    @Override
    public void onSetItems()
    {
        int slot = 1;
        for (Waystone waystone : Waystonez.databaseManager.getPlayerWaystones(player.getUniqueId()))
        {
            final ItemStack item = new ItemStack(Material.LODESTONE);
            final ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("&6" + waystone.getName());
            meta.setLore(List.of("&1X = " + waystone.getLocation().getX() + ", Y = " + waystone.getLocation().getY() + ", Z = " + waystone.getLocation().getZ()));
            item.setItemMeta(meta);
            setItem(slot, item, clicker -> {
                clicker.closeInventory();
                clicker.teleport(waystone.getLocation());
            });
        }
    }
}
