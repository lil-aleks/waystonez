package de.lilaleks.waystonez.util;

import de.lilaleks.waystonez.Waystonez;
import de.lilaleks.waystonez.model.Waystone;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WaystoneDialogs
{
    public static final Dialog NAME_INPUT = RegistryAccess.registryAccess().getRegistry(RegistryKey.DIALOG).get(Key.key("waystonez:name_input"));

    public static Dialog teleportDialog(Player player)
    {
        List<ActionButton> inputs = new ArrayList<>();
        DialogBase.Builder dialogBase = DialogBase.builder(Component.text("Waystonez").color(NamedTextColor.DARK_PURPLE));

        for (Waystone waystone : Waystonez.databaseManager.getPlayerWaystones(player.getUniqueId().toString()))
        {

            // Component.text(waystone.getName()).color(NamedTextColor.GOLD)
            // Component.text("X = " + waystone.getLocation().getX() + ", Y = " + waystone.getLocation().getY() + ", Z = " + waystone.getLocation().getZ()).color(NamedTextColor.DARK_BLUE);

            inputs.add(
                    ActionButton.builder(Component.text(waystone.getName()).color(NamedTextColor.GOLD))
                            .tooltip(Component.text("X = " + waystone.getLocation().getX() + ", Y = " + waystone.getLocation().getY() + ", Z = " + waystone.getLocation().getZ()).color(NamedTextColor.DARK_BLUE))
                            .action(DialogAction.customClick(Key.key("waystonez:teleport/" + waystone.getId()), null))
                            .build()
            );
        }

        return Dialog.create(builder -> builder.empty()
                .base(dialogBase.body(List.of(
                        DialogBody.item(ItemStack.of(Material.LODESTONE)).build(),
                        DialogBody.plainMessage(Component.text("How would you like to call your waypoint?"))
                )).build())
                .type(DialogType.multiAction(inputs).build())
        );
    }
}
