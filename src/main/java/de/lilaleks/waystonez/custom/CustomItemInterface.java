package de.lilaleks.waystonez.custom;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public interface CustomItemInterface
{
    void onRightClick(PlayerInteractEvent event);
    void onLeftClick(PlayerInteractEvent event);
    void onConsume(PlayerItemConsumeEvent event);
    void onPlace(BlockPlaceEvent event);
    void onBreak(BlockBreakEvent event);
}
