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
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WaystoneWand extends CustomItemHandler
{
    public final JavaPlugin plugin;
    public static ItemStack ITEM_STACK = null;
    private int max_uses;

    private static final Map<Player, CompletableFuture<Boolean>> awaitingResponse = new HashMap<>();

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
        itemMeta.lore(List.of(Component.text(max_uses + "/" + max_uses, NamedTextColor.GRAY, Set.of(TextDecoration.BOLD))));
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
        Dialog dialog = WaystoneDialogs.teleportDialog(event.getPlayer());
        CompletableFuture<Boolean> respone = new CompletableFuture<>();
        awaitingResponse.put(event.getPlayer(), respone);
        event.getPlayer().showDialog(dialog);
        if (respone.join())
        {
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
                            itemMeta.lore(List.of(Component.text(finalUses + "/" + max_uses, NamedTextColor.GRAY, Set.of(TextDecoration.BOLD))));
                            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, finalUses);
                        }
                );
            }
        }
    }

    public static void setTeleportResult(Player player, boolean result)
    {
        CompletableFuture<Boolean> future = awaitingResponse.get(player);
        if (future != null)
        {
            future.complete(result);
        }
    }
}
