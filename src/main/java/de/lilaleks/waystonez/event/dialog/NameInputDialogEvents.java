package de.lilaleks.waystonez.event.dialog;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NameInputDialogEvents implements Listener
{
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

            }
        }
    }
}
