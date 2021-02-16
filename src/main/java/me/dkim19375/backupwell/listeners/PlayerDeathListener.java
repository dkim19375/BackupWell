package me.dkim19375.backupwell.listeners;

import me.dkim19375.backupwell.BackupWell;
import me.dkim19375.backupwell.util.ConfigUtils;
import me.dkim19375.backupwell.util.PlayerDeathInformation;
import me.dkim19375.backupwell.util.TimeUtils;
import me.dkim19375.dkim19375core.external.FormattingUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class PlayerDeathListener implements Listener {
    private final BackupWell plugin;

    public PlayerDeathListener(BackupWell plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        ConfigUtils.savePlayerDeath(plugin, e.getEntity());
        final String messageWhenDied = plugin.getConfig().getString("message-when-died");
        if (messageWhenDied == null) {
            return;
        }
        final PlayerDeathInformation info = ConfigUtils.getPlayerDeath(plugin, e.getEntity());
        if (info != null) {
            final Instant time = Instant.ofEpochSecond(plugin.getDeathsFile().getConfig().getLong("uses." + e.getEntity().getUniqueId()));
            if (TimeUtils.getDuration(Instant.now(), time, TimeUnit.DAYS) < 1) {
                return;
            }
        }
        e.getEntity().sendMessage(FormattingUtils.formatWithPAPIAndColors(e.getEntity(), messageWhenDied));
    }
}
