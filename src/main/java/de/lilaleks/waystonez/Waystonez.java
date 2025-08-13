package de.lilaleks.waystonez;

import de.lilaleks.waystonez.custom.block.WaystoneBlock;
import de.lilaleks.waystonez.custom.item.WaystoneWand;
import de.lilaleks.waystonez.database.DatabaseManager;
import de.lilaleks.waystonez.event.CustomItemEventHandler;
import de.lilaleks.waystonez.event.InventoryListener;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Waystonez extends JavaPlugin
{
    public static DatabaseManager databaseManager = null;
    public static List<Locale> SUPPORTED_LANGUAGES = new ArrayList<>()
    {{
        add(Locale.US);
        add(Locale.GERMANY);
    }};

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();

        TranslationStore.StringBased<MessageFormat> tStore = TranslationStore.messageFormat(Key.key("waystonez:messages"));
        registerLang(tStore);
        GlobalTranslator.translator().addSource(tStore);

        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        getServer().getPluginManager().registerEvents(new CustomItemEventHandler(this,
                new WaystoneBlock(this),
                (this.getConfig().getInt("wand_uses", 5) != 0 ? new WaystoneWand(this) : null)
        ), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable()
    {
        databaseManager.close();
    }

    public void registerLang(TranslationStore.StringBased<MessageFormat> tStore)
    {
        for (Locale lang : SUPPORTED_LANGUAGES)
        {
            ResourceBundle bundle = ResourceBundle.getBundle("waystonez.Bundle", lang, UTF8ResourceBundleControl.get());
            tStore.registerAll(lang, bundle, false);
        }
    }
}
