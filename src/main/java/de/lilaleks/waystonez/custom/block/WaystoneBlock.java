package de.lilaleks.waystonez.custom.block;

import de.lilaleks.waystonez.Waystonez;
import de.lilaleks.waystonez.custom.CustomItemHandler;
import de.lilaleks.waystonez.model.Waystone;
import de.lilaleks.waystonez.util.WaystoneDialogs;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;

public class WaystoneBlock extends CustomItemHandler
{
    public final JavaPlugin plugin;
    public static ItemStack ITEM_STACK = null;
    private int maxWaystones;

    public WaystoneBlock(JavaPlugin plugin)
    {
        this.plugin = plugin;

        ItemStack item = new ItemStack(Material.LODESTONE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.text("Waystone").color(NamedTextColor.GOLD));
        CustomModelDataComponent comp = itemMeta.getCustomModelDataComponent();
        comp.setStrings(List.of("waystonez:waystone"));
        itemMeta.setCustomModelDataComponent(comp);
        item.setItemMeta(itemMeta);
        ITEM_STACK = item;

        plugin.getServer().getPluginManager().registerEvents(new Listener()
        {
            @EventHandler
            public void onClick(PlayerInteractEvent event)
            {
                if (event.getClickedBlock() == null)
                    return;
                if (event.getClickedBlock().getBlockData().getMaterial() != Material.LODESTONE)
                    return;
                if (!event.getAction().isRightClick())
                    return;
                Optional<Waystone> waystone = Waystonez.databaseManager.getWaystoneAtLocation(event.getClickedBlock().getLocation());
                if (waystone.isEmpty())
                    return;
                if (!Waystonez.databaseManager.playerHasWaystone(event.getPlayer().getUniqueId().toString(), waystone.get().getId()))
                {
                    event.getPlayer().sendMessage(Component.translatable("waystone.discovered", Component.text(waystone.get().getName(), NamedTextColor.YELLOW, TextDecoration.UNDERLINED)).color(NamedTextColor.GREEN));
                    Waystonez.databaseManager.addDiscoveredWaystone(event.getPlayer().getUniqueId().toString(), waystone.get().getId());
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                } else
                {
                    Dialog dialog = WaystoneDialogs.teleportDialog(event.getPlayer());
                    event.getPlayer().showDialog(dialog);
                }
            }
        }, plugin);

        maxWaystones = plugin.getConfig().getInt("max_waystones", 0);
    }

    @Override
    public ItemStack getItemStack()
    {

        return ITEM_STACK;
    }

    @Override
    public ShapedRecipe getRecipe(JavaPlugin plugin)
    {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "waystone"), getItemStack());

        recipe.shape(
                        "SAS",
                        "ACA",
                        "SES"
                ).setIngredient('S', Material.STONE_BRICKS)
                .setIngredient('E', Material.ENDER_PEARL)
                .setIngredient('C', Material.COMPASS)
                .setIngredient('A', Material.AMETHYST_SHARD);

        return recipe;
    }

    @Override
    public void onPlace(BlockPlaceEvent event)
    {
        if (maxWaystones != 0) {
            if (Waystonez.databaseManager.getWaystoneCount() >= maxWaystones)
            {
                event.getPlayer().sendMessage(Component.translatable("waystone.limit_reached", NamedTextColor.DARK_RED));
                event.setCancelled(true);
                return;
            }
        }

        assert WaystoneDialogs.NAME_INPUT != null;
        event.getPlayer().showDialog(WaystoneDialogs.NAME_INPUT);
    }

    @Override
    public void onBreak(BlockBreakEvent event)
    {
        Optional<Waystone> waystone = Waystonez.databaseManager.getWaystoneAtLocation(event.getBlock().getLocation());

        // isPresent is not needed. if you get an error your server is broken
        if (!waystone.get().getOwnerId().equals(event.getPlayer().getUniqueId().toString()))
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
