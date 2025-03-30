package de.lilaleks.waystonez.block.custom;

import de.lilaleks.waystonez.Waystonez;
import de.lilaleks.waystonez.block.CustomItemHandler;
import de.lilaleks.waystonez.gui.waystone.WaystoneMenu;
import de.lilaleks.waystonez.gui.waystone.menu.NameInputMenu;
import de.lilaleks.waystonez.gui.waystone.menu.TeleportMenu;
import de.lilaleks.waystonez.model.Waystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class WaystoneBlock extends CustomItemHandler
{
    public final JavaPlugin plugin;

    public WaystoneBlock(JavaPlugin plugin)
    {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new Listener()
        {
            @EventHandler
            public void onClick(PlayerInteractEvent event) {
                if (event.getClickedBlock() == null) return;
                if (event.getClickedBlock().getBlockData().getMaterial() != Material.LODESTONE) return;
                Optional<Waystone> waystone = Waystonez.databaseManager.getWaystoneAtLocation(event.getClickedBlock().getLocation());
                if (!waystone.isPresent()) return;
                if (!Waystonez.databaseManager.getPlayerWaystones(event.getPlayer().getUniqueId()).contains(waystone.get())) {
                    Waystonez.databaseManager.addDiscoveredWaystone(event.getPlayer().getUniqueId(), waystone.get().getId());
                } else {
                    new TeleportMenu(event.getPlayer()).open(event.getPlayer());
                }
            }
        }, plugin);
    }

    @Override
    public ItemStack getItemStack()
    {
        ItemStack item = new ItemStack(Material.LODESTONE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("&6Waystone");
        itemMeta.setCustomModelData(714);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public ShapedRecipe getRecipe(JavaPlugin plugin)
    {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "waystone"), getItemStack());

        recipe.shape(
                        "SSS",
                        "SCS",
                        "SES"
                ).setIngredient('S', Material.STONE_BRICKS)
                .setIngredient('E', Material.ENDER_PEARL)
                .setIngredient('C', Material.COMPASS);

        return recipe;
    }

    @Override
    public void onPlace(BlockPlaceEvent event)
    {
        new NameInputMenu(event.getBlock(), plugin).open(event.getPlayer());
    }

    @Override
    public void onBreak(BlockBreakEvent event)
    {
        Optional<Waystone> waystone = Waystonez.databaseManager.getWaystoneAtLocation(event.getBlock().getLocation());

        if (waystone.get().getOwnerId() != event.getPlayer().getUniqueId())
        {
            event.setCancelled(true);
            return;
        }
        Waystonez.databaseManager.deleteWaystone(waystone.get().getId());
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), getItemStack());
    }

    @Override
    public boolean isCustomBlock(Block block)
    {
        return Waystonez.databaseManager.getWaystoneAtLocation(block.getLocation()).isPresent();
    }
}
