package de.lilaleks.waystonez;

import de.lilaleks.waystonez.custom.block.WaystoneBlock;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DialogKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WaystonezBootstrap implements PluginBootstrap
{
    @Override
    public void bootstrap(BootstrapContext context)
    {
        // register nameinput dialog
        context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose()
                .newHandler(event -> event.registry().register(
                        DialogKeys.create(Key.key("waystonez:name_input")),
                        builder -> builder
                                .base(DialogBase.builder(Component.text("Waystones").color(NamedTextColor.DARK_PURPLE))
                                        .canCloseWithEscape(false)
                                        .body(List.of(
                                                DialogBody.item(WaystoneBlock.ITEM_STACK, null, true, true, 100, 100),
                                                DialogBody.plainMessage(Component.text("How would you like to call your waypoint?"))
                                        ))
                                        .inputs(List.of(
                                                DialogInput.text("name", Component.text("Name")).build()
                                        ))
                                        .build()
                                )
                                .type(DialogType.confirmation(
                                        ActionButton.create(
                                                Component.text("Confirm"),
                                                null,
                                                100,
                                                DialogAction.customClick(Key.key("waystonez:name_input/confirm"), null)
                                        ),
                                        ActionButton.create(
                                                Component.text("Cancel"),
                                                null,
                                                100,
                                                DialogAction.customClick(Key.key("waystonez:name_input/cancel"), null)
                                        )
                                ))
                )));

    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context)
    {
        return PluginBootstrap.super.createPlugin(context);
    }
}
