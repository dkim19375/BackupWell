package me.dkim19375.backupwell;

import me.dkim19375.backupwell.commands.CommandHandler;
import me.dkim19375.backupwell.commands.TabCompletionHandler;
import me.dkim19375.backupwell.listeners.PlayerDeathListener;
import me.dkim19375.backupwell.listeners.PlayerMoveListener;
import me.dkim19375.backupwell.placeholders.WellPlaceholderExpansion;
import me.dkim19375.backupwell.util.ConfigUtils;
import me.dkim19375.backupwell.util.PlayerDeathInformation;
import me.dkim19375.backupwell.util.TimeUtils;
import me.dkim19375.backupwell.util.Well;
import me.dkim19375.dkim19375core.ConfigFile;
import me.dkim19375.dkim19375core.CoreJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class BackupWell extends CoreJavaPlugin {
    private ConfigFile deathsFile;
    private ConfigFile wellsFile;
    private final WellPlaceholderExpansion expansion = new WellPlaceholderExpansion(this);

    @Override
    public void onEnable() {
        register();
        saveConfigs();
    }

    @Override
    public void onDisable() {
        expansion.unregister();
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
        expansion.register();
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (final Map.Entry<UUID, Instant> use : ConfigUtils.getUses(this).entrySet()) {
                final Duration time = TimeUtils.getTimeUntilExpires(use.getValue());
                if (time == null) {
                    final FileConfiguration fileConfiguration = getDeathsFile().getConfig();
                    fileConfiguration.set("uses." + use.getKey().toString(), null);
                    getDeathsFile().save();
                }
            }
        }, 20L, 20L);
    }

    public ConfigFile getDeathsFile() {
        return deathsFile;
    }

    public ConfigFile getWellsFile() {
        return wellsFile;
    }
}
