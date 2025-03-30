package de.lilaleks.waystonez;

import de.lilaleks.waystonez.block.CustomItemHandler;
import de.lilaleks.waystonez.block.custom.WaystoneBlock;
import de.lilaleks.waystonez.database.DatabaseManager;
import de.lilaleks.waystonez.event.CustomItemEventHandler;
import de.lilaleks.waystonez.event.InventoryListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Waystonez extends JavaPlugin
{
    public static DatabaseManager databaseManager = null;
    public CustomItemHandler[] customItems = {
            new WaystoneBlock(this),
    };

    @Override
    public void onEnable()
    {
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        getServer().getPluginManager().registerEvents(new CustomItemEventHandler(this,
                customItems
        ), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }
}
