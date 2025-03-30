package de.lilaleks.waystonez.block;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public interface CustomItemInterface
{
    void onRightClick(PlayerInteractEvent event);
    void onLeftClick(PlayerInteractEvent event);
    void onPlace(BlockPlaceEvent event);
    void onBreak(BlockBreakEvent event);
}
