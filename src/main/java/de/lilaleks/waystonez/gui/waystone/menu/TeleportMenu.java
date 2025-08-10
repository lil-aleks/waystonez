package de.lilaleks.waystonez.gui.waystone.menu;

import de.lilaleks.waystonez.Waystonez;
import de.lilaleks.waystonez.gui.waystone.WaystoneMenu;
import de.lilaleks.waystonez.model.Waystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TeleportMenu extends WaystoneMenu
{
    public final Player player;
    public TeleportMenu(Player player)
    {
        super(((Waystonez.databaseManager.getPlayerWaystones(player.getUniqueId().toString()).size() / 9) + 1) * 9);
        this.player = player;
    }

    @Override
    public void onSetItems()
    {
        int slot = 0;
        for (Waystone waystone : Waystonez.databaseManager.getPlayerWaystones(player.getUniqueId().toString()))
        {
            final ItemStack item = new ItemStack(Material.LODESTONE);
            final ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(waystone.getName()).color(NamedTextColor.GOLD));
            meta.setLore(List.of("§1X = " + waystone.getLocation().getX() + ", Y = " + waystone.getLocation().getY() + ", Z = " + waystone.getLocation().getZ()));
            meta.lore(List.of(Component.text("X = " + waystone.getLocation().getX() + ", Y = " + waystone.getLocation().getY() + ", Z = " + waystone.getLocation().getZ()).color(NamedTextColor.DARK_BLUE)));
            item.setItemMeta(meta);
            setItem(slot, item, clicker -> {
                clicker.closeInventory();
                clicker.teleport(waystone.getLocation().add(0.5,1,0.5));
                clicker.playSound(clicker.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
                clicker.playSound(clicker.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.1f, 1.5f);
                clicker.getWorld().spawnParticle(Particle.PORTAL, clicker.getLocation().add(0,1,0), 50, 0.5, 1, 0.5, 0.1);
                clicker.getWorld().spawnParticle(Particle.END_ROD, clicker.getLocation().add(0,1,0), 20, 0.5, 1, 0.5, 0.05);
            });
            slot++;
        }
    }
}
