package de.lilaleks.waystonez.gui.waystone.menu;

import de.lilaleks.waystonez.Waystonez;
import de.lilaleks.waystonez.custom.block.WaystoneBlock;
import de.lilaleks.waystonez.gui.waystone.WaystoneMenu;
import de.lilaleks.waystonez.model.Waystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public class NameInputMenu extends WaystoneMenu
{
    private final Block block;
    private final JavaPlugin plugin;
    private int maxWaystones;

    public NameInputMenu(Block block, JavaPlugin plugin)
    {
        super(9);
        this.block = block;
        this.plugin = plugin;
        maxWaystones = plugin.getConfig().getInt("max_waystones", 0);
    }

    @Override
    public void onSetItems()
    {
        final ItemStack nameItem = new ItemStack(Material.PAPER);
        final ItemMeta itemMeta = nameItem.getItemMeta();
        itemMeta.displayName(Component.text("Waystone", NamedTextColor.BLUE));
        nameItem.setItemMeta(itemMeta);
        setItem(4, nameItem, player ->
        {
            player.closeInventory();

            BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () ->
            {
                player.sendMessage(Component.newline().appendNewline().appendNewline().appendNewline().append(Component.translatable("waystone.choose_name", NamedTextColor.RED)));
            }, 0, 60);
            Bukkit.getPluginManager().registerEvents(new Listener()
            {
                @EventHandler
                public void onChat(AsyncPlayerChatEvent event)
                {
                    if (event.getPlayer() != player)
                        return;
                    event.setCancelled(true);
                    if (maxWaystones != 0)
                    {
                        if (Waystonez.databaseManager.getWaystoneCount() >= maxWaystones)
                        {
                            event.getPlayer().sendMessage(Component.translatable("waystone.limit_reached", NamedTextColor.DARK_RED));
                            block.getWorld().dropItemNaturally(block.getLocation(), WaystoneBlock.ITEM_STACK);
                            block.setType(Material.AIR);
                            HandlerList.unregisterAll(this);
                            return;
                        }
                    }
                    String name = event.getMessage();
                    Waystonez.databaseManager.saveWaystone(new Waystone(name, block.getLocation(), player.getUniqueId().toString()));
                    player.sendMessage(Component.translatable("waystone.named", NamedTextColor.GREEN, Component.text(name).decorate(TextDecoration.UNDERLINED).color(NamedTextColor.GOLD)));
                    player.playSound(event.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 0.5f);
                    player.playSound(event.getPlayer().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 0.5f);
                    player.getWorld().spawnParticle(Particle.ENCHANT, block.getLocation().add(0.5, 0.5, 0.5), 50, 0.5, 0.5, 0.5, 0.5);
                    player.getWorld().spawnParticle(Particle.END_ROD, block.getLocation().add(0.5, 0.5, 0.5), 20, 0.5, 0.5, 0.5, 0.1);

                    boolean discoveryRequired = plugin.getConfig().getBoolean("discovery_required", true);

                    if (!discoveryRequired)
                    {
                        int id = Waystonez.databaseManager.getWaystoneAtLocation(block.getLocation()).get().getId();
                        Bukkit.getOnlinePlayers().forEach(p ->
                        {
                            if (p != player)
                            {
                                Waystonez.databaseManager.addDiscoveredWaystone(p.getUniqueId().toString(), id);
                            }
                        });
                    }

                    task.cancel();
                    HandlerList.unregisterAll(this);
                }

                @EventHandler
                public void onBreak(BlockBreakEvent event)
                {
                    if (event.getBlock().getLocation() != block.getLocation())
                        return;

                    task.cancel();
                    event.setDropItems(false);
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), WaystoneBlock.ITEM_STACK);
                    HandlerList.unregisterAll(this);
                }

                @EventHandler
                public void onSneak(PlayerToggleSneakEvent event)
                {
                    if (event.getPlayer() != player)
                        return;
                    if (event.isSneaking())
                    {
                        task.cancel();
                        event.getPlayer().getWorld().dropItemNaturally(block.getLocation(), WaystoneBlock.ITEM_STACK);
                        event.getPlayer().getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
                        HandlerList.unregisterAll(this);
                    }
                }

                @EventHandler
                public void onMove(PlayerMoveEvent event)
                {
                    if (event.getPlayer() == player)
                        event.setCancelled(true);
                }
            }, plugin);
        });
    }

    @Override
    public void close(InventoryCloseEvent event)
    {
        if (event.getReason() == InventoryCloseEvent.Reason.PLAYER)
        {
            event.getPlayer().getWorld().dropItemNaturally(block.getLocation(), WaystoneBlock.ITEM_STACK);
            event.getPlayer().getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
        }
    }
}
