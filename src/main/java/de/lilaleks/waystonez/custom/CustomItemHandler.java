package de.lilaleks.waystonez.custom;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomItemHandler implements CustomItemInterface {

    public abstract ItemStack getItemStack();

    public abstract ShapedRecipe getRecipe(JavaPlugin plugin);

    public boolean isCustomBlock(Block block) { return false; }

    @Override
    public void onRightClick(PlayerInteractEvent event) {}

    @Override
    public void onLeftClick(PlayerInteractEvent event) {}
    @Override
    public void onConsume(PlayerItemConsumeEvent event) {}

    @Override
    public void onPlace(BlockPlaceEvent event) {}

    @Override
    public void onBreak(BlockBreakEvent event) {}
}
