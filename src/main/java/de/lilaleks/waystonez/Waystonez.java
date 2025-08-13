package de.lilaleks.waystonez;

import de.lilaleks.waystonez.custom.block.WaystoneBlock;
import de.lilaleks.waystonez.custom.item.WaystoneWand;
import de.lilaleks.waystonez.database.DatabaseManager;
import de.lilaleks.waystonez.event.CustomItemEventHandler;
import de.lilaleks.waystonez.event.DialogEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
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
    public static List<Locale> SUPPORTED_LANGUAGES = new ArrayList<>(){{
        add(Locale.US);
        add(Locale.GERMAN);
    }};

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();

        // Add translations
        TranslationStore.StringBased<MessageFormat> tStore = TranslationStore.messageFormat(Key.key("waystonez:messages"));
        registerLang(tStore);
        GlobalTranslator.translator().addSource(tStore);

        // Connect with database
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        // Register events
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

    public void registerLang(TranslationStore.StringBased<MessageFormat> tStore) {
        for (Locale lang : SUPPORTED_LANGUAGES) {
            ResourceBundle bundle = ResourceBundle.getBundle("waystonez.Bundle", lang, UTF8ResourceBundleControl.get());
            tStore.registerAll(lang, bundle, false);
        }
    }

}
