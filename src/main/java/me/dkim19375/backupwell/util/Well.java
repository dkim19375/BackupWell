package me.dkim19375.backupwell.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Well implements ConfigurationSerializable, Cloneable {
    @NotNull
    private final String name;
    @NotNull
    private final BoundingBox boundingBox;
    @NotNull
    private final World world;

    public Well(@NotNull final String name, @NotNull final BoundingBox boundingBox, @NotNull final World world) {
        this.name = name;
        this.boundingBox = boundingBox;
        this.world = world;
    }

    @Nullable
    public static Well getWellByName(@NotNull final Set<Well> wells, @NotNull final String name) {
        for (Well well : wells) {
            if (well.getName().equalsIgnoreCase(name)) {
                return well;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("boundingbox", boundingBox);
        map.put("world", world.getName());
        return map;
    }

    @SuppressWarnings("unused")
    public static Well deserialize(@NotNull final Map<String, Object> map) {
        try {
            return new Well((String) map.get("name"), (BoundingBox) map.get("boundingbox"),
                    Objects.requireNonNull(Bukkit.getWorld((String) map.get("world"))));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Well clone() {
        return new Well(name, boundingBox, world);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @NotNull
    public World getWorld() {
        return world;
    }
}
