package de.lilaleks.waystonez.custom.item;

import de.lilaleks.waystonez.custom.CustomItemHandler;
import de.lilaleks.waystonez.util.WaystoneDialogs;
import io.papermc.paper.dialog.Dialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class WaystoneWand extends CustomItemHandler
{
    public final JavaPlugin plugin;
    public static ItemStack ITEM_STACK = null;
    private int max_uses;

    public WaystoneWand(JavaPlugin plugin)
    {
        this.plugin = plugin;
        max_uses = plugin.getConfig().getInt("wand_uses", 5);

        ItemStack item = new ItemStack(Material.POISONOUS_POTATO);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.text("Waystone Wand").color(NamedTextColor.LIGHT_PURPLE));
        CustomModelDataComponent comp = itemMeta.getCustomModelDataComponent();
        comp.setStrings(List.of("waystonez:waystone_wand"));
        itemMeta.setCustomModelDataComponent(comp);
        itemMeta.setItemModel(NamespacedKey.minecraft("amethyst_shard"));
        itemMeta.lore(List.of(Component.text(max_uses).decorate(TextDecoration.BOLD).append(Component.text("/" + max_uses + " uses left")).color(NamedTextColor.GRAY)));
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "uses");

        data.set(key, PersistentDataType.INTEGER, max_uses);

        item.setItemMeta(itemMeta);

        ITEM_STACK = item;
    }

    @Override
    public ItemStack getItemStack()
    {

        return ITEM_STACK;
    }

    @Override
    public ShapedRecipe getRecipe(JavaPlugin plugin)
    {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "waystone_wand"), getItemStack());

        recipe.shape(
                        "A",
                        "E",
                        "A"
                ).setIngredient('E', Material.ENDER_PEARL)
                .setIngredient('A', Material.AMETHYST_SHARD);

        return recipe;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event)
    {
        event.setCancelled(true);
        NamespacedKey key = new NamespacedKey(plugin, "uses");
        int uses = event.getItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        uses--;
        if (uses == 0)
        {
            event.getPlayer().getInventory().remove(event.getItem());
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_SHIELD_BREAK, 0.6f, 0.8f);
        } else
        {
            int finalUses = uses;
            event.getItem().editMeta(itemMeta ->
                    {
                        itemMeta.lore(List.of(Component.text(finalUses).decorate(TextDecoration.BOLD).append(Component.text("/" + max_uses + " uses left")).color(NamedTextColor.GRAY)));
                        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, finalUses);
                    }
            );
        }
        Dialog dialog = WaystoneDialogs.teleportDialog(event.getPlayer());
        event.getPlayer().showDialog(dialog);
    }
}
