package me.dkim19375.backupwell.util;

import me.dkim19375.backupwell.BackupWell;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;

public class ConfigUtils {
    public static void savePlayerDeath(BackupWell plugin, Player player) {
        plugin.getDeathsFile().getConfig().set("deaths." + player.getUniqueId().toString(), null);
        plugin.getDeathsFile().getConfig().set("deaths." + player.getUniqueId().toString(),
                new PlayerDeathInformation(player.getLocation(), player.getUniqueId()));
        plugin.getDeathsFile().save();
    }

    @Nullable
    public static PlayerDeathInformation getPlayerDeath(BackupWell plugin, Player player) {
        return getPlayerDeath(plugin, player.getUniqueId());
    }

    @Nullable
    public static PlayerDeathInformation getPlayerDeath(BackupWell plugin, UUID player) {
        final FileConfiguration fileConfiguration = plugin.getDeathsFile().getConfig();
        final PlayerDeathInformation info = fileConfiguration.getSerializable("deaths." + player.toString(), PlayerDeathInformation.class);
        if (info == null) {
            if (fileConfiguration.contains("deaths." + player.toString())) {
                fileConfiguration.set("deaths." + player.toString(), null);
                plugin.getDeathsFile().save();
            }
        }
        return info;
    }

    @NotNull
    public static Set<Well> getWells(BackupWell plugin) {
        final Set<Well> wells = new HashSet<>();
        final ConfigurationSection section = plugin.getWellsFile().getConfig().getConfigurationSection("wells");
        if (section == null) {
            return wells;
        }
        for (final String key : section.getKeys(false)) {
            try {
                if (section.getSerializable(key, Well.class) == null) {
                    throw new NullPointerException();
                }
                wells.add(section.getSerializable(key, Well.class));
            } catch (Exception ignored) {
                section.set(key, null);
                plugin.getWellsFile().save();
            }
        }
        return wells;
    }

    @NotNull
    public static Set<Well> getWellsInLocation(Set<Well> wells, Location location) {
        final Set<Well> containedRegions = new HashSet<>();
        for (final Well well : wells) {
            if (!well.getWorld().getName().equals(Objects.requireNonNull(location.getWorld()).getName())) {
                continue;
            }
            final BoundingBox wellBB = well.getBoundingBox();
            final Vector max = wellBB.getMax().clone();
            max.setX(wellBB.getMaxX() + 0.9);
            max.setZ(wellBB.getMaxZ() + 0.9);
            if (location.toVector().isInAABB(wellBB.getMin(), max)) {
                containedRegions.add(well);
            }
        }
        return containedRegions;
    }

    public static void addWell(BackupWell plugin, Well well) {
        final ConfigurationSection section;
        if (getWells(plugin).size() < 1) {
            section = plugin.getWellsFile().getConfig().createSection("wells");
        } else {
            section = Objects.requireNonNull(plugin.getWellsFile().getConfig().getConfigurationSection("wells"));
        }
        section.set(well.getName(), well);
        plugin.getWellsFile().save();
    }

    public static void removeWell(BackupWell plugin, Well well) {
        final ConfigurationSection section;
        if (getWells(plugin).size() < 1) {
            section = plugin.getWellsFile().getConfig().createSection("wells");
        } else {
            section = Objects.requireNonNull(plugin.getWellsFile().getConfig().getConfigurationSection("wells"));
        }
        boolean save = false;
        for (String key : section.getKeys(false)) {
            final Well r = section.getSerializable(key, Well.class);
            if (r == null) {
                section.set(key, null);
                save = true;
                continue;
            }
            if (r.getBoundingBox().equals(well.getBoundingBox())) {
                section.set(key, null);
                save = true;
                continue;
            }
            if (r.getWorld().getName().equals(well.getWorld().getName())) {
                section.set(key, null);
                save = true;
                continue;
            }
            if (key.equalsIgnoreCase(well.getName())) {
                section.set(key, null);
                save = true;
            }
        }
        if (save) {
            plugin.getWellsFile().save();
        }
    }

    @NotNull
    public static Map<UUID, Instant> getUses(BackupWell plugin) {
        final Map<UUID, Instant> map = new HashMap<>();
        final ConfigurationSection section = plugin.getDeathsFile().getConfig().getConfigurationSection("uses");
        if (section == null) {
            return map;
        }
        for (String str : section.getKeys(false)) {
            try {
                final UUID uuid = UUID.fromString(str);
                final Instant instant = Instant.ofEpochSecond(section.getLong(str));
                map.put(uuid, instant);
            } catch (IllegalArgumentException | NullPointerException ignored) {}
        }
        return map;
    }
}
