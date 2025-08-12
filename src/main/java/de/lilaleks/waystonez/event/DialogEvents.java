package de.lilaleks.waystonez.event;

import de.lilaleks.waystonez.Waystonez;
import de.lilaleks.waystonez.custom.block.WaystoneBlock;
import de.lilaleks.waystonez.model.Waystone;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class DialogEvents implements Listener
{
    private final Waystonez plugin;
    private final int maxWaystones;

    public DialogEvents(Waystonez plugin)
    {
        this.plugin = plugin;
        maxWaystones = plugin.getConfig().getInt("max_waystones", 0);
    }

    @EventHandler
    public void handleDialog(PlayerCustomClickEvent event)
    {
        if (event.getIdentifier().equals(Key.key("waystonez:name_input/confirm")))
        {
            DialogResponseView view = event.getDialogResponseView();
            if (view == null)
            {
                return;
            }
            String name = view.getText("name");

            if (event.getCommonConnection() instanceof PlayerGameConnection conn)
            {
                Player player = conn.getPlayer();
                Block block = player.getTargetBlockExact(10);
                if (maxWaystones != 0)
                {
                    if (Waystonez.databaseManager.getWaystoneCount() >= maxWaystones)
                    {
                        player.sendMessage(Component.text("The server has reached the max amount of waystones.").color(NamedTextColor.DARK_RED));
                        block.getWorld().dropItemNaturally(block.getLocation(), WaystoneBlock.ITEM_STACK);
                        block.setType(Material.AIR);
                        return;
                    }
                }
                Waystonez.databaseManager.saveWaystone(new Waystone(name, block.getLocation(), player.getUniqueId().toString()));
                player.sendMessage(Component.text("You named your waytone: ").color(NamedTextColor.GREEN).append(Component.text(name).decorate(TextDecoration.UNDERLINED).color(NamedTextColor.GOLD)));
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 0.5f);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 0.5f);
                player.getWorld().spawnParticle(Particle.ENCHANT, block.getLocation().add(0.5, 0.5, 0.5), 50, 0.5, 0.5, 0.5, 0.5);
                player.getWorld().spawnParticle(Particle.END_ROD, block.getLocation().add(0.5, 0.5, 0.5), 20, 0.5, 0.5, 0.5, 0.1);

            }
        } else if (event.getIdentifier().equals(Key.key("waystonez:name_input/cancel")))
        {
            if (event.getCommonConnection() instanceof PlayerGameConnection conn)
            {
                Player player = conn.getPlayer();
                Block block = player.getTargetBlockExact(10);
                block.getWorld().dropItemNaturally(block.getLocation(), WaystoneBlock.ITEM_STACK);
                block.setType(Material.AIR);
            }

        } else if (event.getIdentifier().key().asString().startsWith("waystonez:teleport/"))
        {
            int id = Integer.parseInt(event.getIdentifier().key().asString().substring("waystonez:teleport/".length()));

            if (event.getCommonConnection() instanceof PlayerGameConnection conn)
            {
                Player player = conn.getPlayer();
                Optional<Waystone> waystone = Waystonez.databaseManager.getWaystone(id);
                if (waystone.isPresent())
                {
                    player.teleport(waystone.get().getLocation().add(0.5, 1, 0.5));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
                    player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.1f, 1.5f);
                    player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 50, 0.5, 1, 0.5, 0.1);
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 20, 0.5, 1, 0.5, 0.05);
                }

            }

        }
    }
}
