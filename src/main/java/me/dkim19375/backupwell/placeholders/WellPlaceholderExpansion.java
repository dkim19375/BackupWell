package me.dkim19375.backupwell.placeholders;

import me.dkim19375.backupwell.BackupWell;
import me.dkim19375.backupwell.util.TimeUtils;
import me.dkim19375.dkim19375core.external.PAPIExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

public class WellPlaceholderExpansion extends PAPIExpansion {
    private final BackupWell plugin;

    public WellPlaceholderExpansion(@NotNull BackupWell plugin) {
        super(plugin, null, "dkim19375", null);
        this.plugin = plugin;
    }

    @Override
    @Nullable
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        final String[] args = params.contains("_")
                ? params.toLowerCase().split("_")
                : Collections.singletonList(params).toArray(new String[0]);
        final Duration timeLeft = TimeUtils.getTimeUntilExpires(Instant
                .ofEpochSecond(plugin.getDeathsFile().getConfig().getLong("uses." + player.getUniqueId().toString())));
        if (args[0].startsWith("timeleft")) {
            if (timeLeft == null) {
                plugin.getDeathsFile().getConfig().set("uses." + player.getUniqueId().toString(), null);
                plugin.getDeathsFile().save();
                return "";
            }
            if (plugin.getDeathsFile().getConfig().getLong("uses." + player.getUniqueId().toString()) == 0) {
                return "";
            }
            return ChatColor.YELLOW + TimeUtils.formatNumbers(timeLeft.toSeconds());
        }
        if (args[0].startsWith("canuse")) {
            return plugin.getDeathsFile().getConfig().getLong("uses." + player.getUniqueId().toString()) == 0
                    ? ChatColor.GREEN + "Available"
                    : ChatColor.RED + "Unavailable";
        }
        return "null";
    }
}
