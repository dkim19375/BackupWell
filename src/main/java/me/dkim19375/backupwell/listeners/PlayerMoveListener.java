package me.dkim19375.backupwell.listeners;

import me.dkim19375.backupwell.BackupWell;
import me.dkim19375.backupwell.util.ConfigUtils;
import me.dkim19375.backupwell.util.PlayerDeathInformation;
import me.dkim19375.backupwell.util.TimeUtils;
import me.dkim19375.backupwell.util.Well;
import me.dkim19375.dkim19375core.external.FormattingUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class PlayerMoveListener implements Listener {
    private final BackupWell plugin;

    public PlayerMoveListener(BackupWell plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        Well well = null;
        if (ConfigUtils.getWellsInLocation(ConfigUtils.getWells(plugin), e.getFrom()).size() < 1) {
            return;
        }
        for (Well w : ConfigUtils.getWellsInLocation(ConfigUtils.getWells(plugin), e.getFrom())) {
            well = w;
            break;
        }
        if (well == null) {
            return;
        }
        //noinspection ConstantConditions
        final PlayerDeathInformation info = ConfigUtils.getPlayerDeath(plugin, e.getPlayer()) == null ? null
                : ConfigUtils.getPlayerDeath(plugin, e.getPlayer()).clone();
        if (info == null) {
            e.getPlayer().sendMessage(format(e.getPlayer(), plugin.getConfig().getString("message-when-no-death")));
            for (String s : plugin.getConfig().getStringList("commands-when-denied")) {
                dispatchCommand(e.getPlayer(), s);
            }
            final Location loc = e.getPlayer().getLocation().clone();
            loc.setYaw(plugin.getConfig().getInt("direction"));
            e.getPlayer().teleport(loc);
            final String stringSound = plugin.getConfig().getString("sound");
            try {
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.valueOf(stringSound), 1.0f, 1.0f);
            } catch (IllegalArgumentException ignored) {
                Bukkit.getLogger().log(Level.SEVERE, stringSound + " isn't a valid sound!");
            }
            return;
        }
        final long uses = plugin.getDeathsFile().getConfig().getLong("uses." + e.getPlayer().getUniqueId());
        if (uses != 0) {
            final Instant time = Instant.ofEpochSecond(uses);
            if (TimeUtils.getTimeUntilExpires(time) != null) {
                e.getPlayer().sendMessage(format(e.getPlayer(), plugin.getConfig().getString("message-when-cooldown")).replace("%time%",
                        TimeUtils.formatNumbers(TimeUtils.getDurationUntilMidnight(Instant.now()).toSeconds())));
                for (String s : plugin.getConfig().getStringList("commands-when-denied")) {
                    dispatchCommand(e.getPlayer(), s);
                }
                return;
            }
        }
        // used
        e.getPlayer().teleport(info.getLocation());
        e.getPlayer().sendMessage(format(e.getPlayer(), plugin.getConfig().getString("message-when-use")));
        final FileConfiguration fileConfiguration = plugin.getDeathsFile().getConfig();
        fileConfiguration.set("deaths." +  e.getPlayer().getUniqueId(), null);
        fileConfiguration.set("uses." +  e.getPlayer().getUniqueId(), Instant.now().getEpochSecond());
        plugin.getDeathsFile().save();
        final ConfigurationSection effects = plugin.getConfig().getConfigurationSection("effects");
        if (effects == null) {
            return;
        }
        boolean save = false;
        final Set<PotionEffect> effectSet = new HashSet<>();
        for (String effectId : effects.getKeys(false)) {
            final String effectType = effects.getString(effectId + ".type");
            if (effectType == null) {
                effects.set(effectId + ".type", "INVALID EFFECT TYPE");
                save = true;
                continue;
            }
            final PotionEffectType type = PotionEffectType.getByName(effectType);
            if (type == null) {
                effects.set(effectId + ".type", "INVALID EFFECT TYPE");
                save = true;
                continue;
            }
            final PotionEffect effect = new PotionEffect(type, effects.getInt(effectId + ".duration"), effects.getInt(effectId + ".amplifier") - 1);
            effectSet.add(effect);
        }
        for (PotionEffect effect : effectSet) {
            e.getPlayer().addPotionEffect(effect);
        }
        e.getPlayer().addPotionEffects(effectSet);
        if (save) {
            plugin.saveConfig();
        }
    }

    private void dispatchCommand(final Player p, String command) {
        final boolean op = p.isOp();
        p.setOp(true);
        if (!command.toLowerCase().startsWith("tp")) {
            Bukkit.dispatchCommand(p, command);
            p.setOp(op);
            return;
        }
        String[] coords = command.split(" ");
        p.teleport(new Location(p.getWorld(), Integer.parseInt(coords[1]),
                Integer.parseInt(coords[2]), Integer.parseInt(coords[3])));
        p.setOp(op);
    }

    @NotNull
    private String format(final Player p, final String string) {
        if (string == null) {
            return "";
        }
        return FormattingUtils.formatWithPAPIAndColors(p, string);
    }
}
