package de.lilaleks.waystonez.util;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

public class WaystoneDialogs
{
    public static final Dialog NAME_INPUT = RegistryAccess.registryAccess().getRegistry(RegistryKey.DIALOG).get(Key.key("waystonez:name_input"));
    public static Dialog teleportDialog(Player player) {
        return NAME_INPUT;
    }
}
