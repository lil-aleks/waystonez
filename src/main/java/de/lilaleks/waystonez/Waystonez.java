package de.lilaleks.waystonez;

import de.lilaleks.waystonez.custom.block.WaystoneBlock;
import de.lilaleks.waystonez.custom.item.WaystoneWand;
import de.lilaleks.waystonez.database.DatabaseManager;
import de.lilaleks.waystonez.event.CustomItemEventHandler;
import de.lilaleks.waystonez.event.InventoryListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Waystonez extends JavaPlugin
{
    public static DatabaseManager databaseManager = null;

    @Override
    public void onEnable()
    {
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        getServer().getPluginManager().registerEvents(new CustomItemEventHandler(this,
                new WaystoneBlock(this),
                ( this.getConfig().getInt("wand_uses", 5) != 0 ? new WaystoneWand(this) : null)
        ), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable()
    {
        databaseManager.close();
    }
}
