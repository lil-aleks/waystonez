package de.lilaleks.waystonez;

import de.lilaleks.waystonez.custom.block.WaystoneBlock;
import de.lilaleks.waystonez.custom.item.WaystoneWand;
import de.lilaleks.waystonez.database.DatabaseManager;
import de.lilaleks.waystonez.event.CustomItemEventHandler;
import de.lilaleks.waystonez.event.DialogEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class Waystonez extends JavaPlugin
{
    public static DatabaseManager databaseManager = null;

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        getServer().getPluginManager().registerEvents(new CustomItemEventHandler(this,
                new WaystoneBlock(this),
                (this.getConfig().getInt("wand_uses", 5) != 0 ? new WaystoneWand(this) : null) // disable wand if no uses
        ), this);
        getServer().getPluginManager().registerEvents(new DialogEvents(this), this);
    }

    @Override
    public void onDisable()
    {
        databaseManager.close();
    }

}
