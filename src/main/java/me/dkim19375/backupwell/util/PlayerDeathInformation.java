package me.dkim19375.backupwell.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDeathInformation implements ConfigurationSerializable, Cloneable {
    @NotNull
    private final Location location;
    @NotNull
    private final UUID player;

    public PlayerDeathInformation(@NotNull Location location, @NotNull UUID player) {
        this.location = location;
        this.player = player;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("player", player.toString());
        return map;
    }

    @SuppressWarnings("unused")
    @Nullable
    public static PlayerDeathInformation deserialize(@NotNull final Map<String, Object> map) {
        try {
            final Location loc = (Location) map.get("location");
            if (loc == null) {
                return null;
            }
            final UUID uuid = Bukkit.getOfflinePlayer(UUID.fromString((String) map.get("uuid"))).getUniqueId();
            return new PlayerDeathInformation(loc,
                    uuid);
        } catch (Exception ignored) {
            return null;
        }
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    @NotNull
    public UUID getPlayer() {
        return player;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    @NotNull
    public PlayerDeathInformation clone() {
        return new PlayerDeathInformation(location, player);
    }
}
