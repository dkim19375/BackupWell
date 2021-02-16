package me.dkim19375.backupwell;

import me.dkim19375.backupwell.commands.CommandHandler;
import me.dkim19375.backupwell.commands.TabCompletionHandler;
import me.dkim19375.backupwell.listeners.PlayerDeathListener;
import me.dkim19375.backupwell.listeners.PlayerMoveListener;
import me.dkim19375.backupwell.util.PlayerDeathInformation;
import me.dkim19375.backupwell.util.Well;
import me.dkim19375.dkim19375core.ConfigFile;
import me.dkim19375.dkim19375core.CoreJavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.logging.Level;

public class BackupWell extends CoreJavaPlugin {
    private ConfigFile deathsFile;
    private ConfigFile wellsFile;

    @Override
    public void onEnable() {
        register();
        saveConfigs();
    }

    @Override
    public void onDisable() {

    }

    public void saveConfigs() {
        saveDefaultConfig();
        deathsFile = new ConfigFile(this, "deaths.yml");
        wellsFile = new ConfigFile(this, "wells.yml");
        deathsFile.createConfig();
        wellsFile.createConfig();
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        super.reloadConfig();
        deathsFile.createConfig();
        wellsFile.createConfig();
        deathsFile.reload();
        wellsFile.reload();
    }

    private void register() {
        //noinspection SpellCheckingInspection
        final PluginCommand command = getCommand("backupwell");
        if (command == null) {
            log(Level.SEVERE, "Could not register command!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        ConfigurationSerialization.registerClass(Well.class);
        ConfigurationSerialization.registerClass(PlayerDeathInformation.class);
        command.setExecutor(new CommandHandler(this));
        command.setTabCompleter(new TabCompletionHandler());
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
    }

    public ConfigFile getDeathsFile() {
        return deathsFile;
    }

    public ConfigFile getWellsFile() {
        return wellsFile;
    }
}
