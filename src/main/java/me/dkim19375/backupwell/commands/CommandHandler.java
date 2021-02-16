package me.dkim19375.backupwell.commands;

import me.dkim19375.backupwell.BackupWell;
import me.dkim19375.backupwell.util.ConfigUtils;
import me.dkim19375.backupwell.util.Well;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class CommandHandler implements CommandExecutor {
    private final BackupWell plugin;
    private final String LITTLE_ARGS = ChatColor.RED + "Not enough arguments!";
    private final String MANY_ARGS = ChatColor.RED + "Too many arguments!";
    private final String MUST_BE_PLAYER = ChatColor.RED + "You must be a player!";
    private final String INVALID_ARGUMENT = ChatColor.RED + "Invalid argument!";

    public CommandHandler(BackupWell plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            showHelp(sender, label);
            sender.sendMessage(LITTLE_ARGS);
            return true;
        }
        if (args.length > 5) {
            showHelp(sender, label);
            sender.sendMessage(MANY_ARGS);
        }
        switch (args[0].toLowerCase()) {
            case "help":
                showHelp(sender, label);
                return true;
            case "reload":
                sender.sendMessage(ChatColor.GOLD + "Reloading all configuration files");
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded all configuration files!");
                return true;
            case "list":
                if (args.length > 1) {
                    showHelp(sender, label);
                    sender.sendMessage(MANY_ARGS);
                }
                sender.sendMessage(ChatColor.GOLD + "----------------------------");
                sender.sendMessage(ChatColor.GREEN + "Wells:");
                sender.sendMessage(ChatColor.GOLD + "----------------------------");
                for (Well well : ConfigUtils.getWells(plugin)) {
                    sender.sendMessage(ChatColor.AQUA + "- " + well.getName() + ", Location: "
                            + Objects.requireNonNull(well.getWorld()).getName() + "\nPos1: "
                            + well.getBoundingBox().getMaxX() + ", "
                            + well.getBoundingBox().getMaxY() + ", "
                            + well.getBoundingBox().getMaxZ() + "\nPos2: "
                            + well.getBoundingBox().getMaxX() + ", "
                            + well.getBoundingBox().getMinY() + ", "
                            + well.getBoundingBox().getMaxZ());
                    sender.sendMessage(ChatColor.GOLD + "----------------------------");
                }
                return true;
            case "well":
                switch (args[1].toLowerCase()) {
                    case "create":
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(MUST_BE_PLAYER);
                            return true;
                        }
                        if (args.length < 5) {
                            showHelp(sender, label);
                            sender.sendMessage(LITTLE_ARGS);
                        }
                        final Player p = (Player) sender;
                        final Well wellCreateCheck = Well.getWellByName(ConfigUtils.getWells(plugin), args[2]);
                        if (wellCreateCheck != null) {
                            sender.sendMessage(ChatColor.RED + "The well " + args[2] + " already exists!");
                            return true;
                        }
                        final Location pos1 = getLocationFromString(args[3], p.getWorld());
                        final Location pos2 = getLocationFromString(args[4], p.getWorld());
                        if (pos1 == null) {
                            sender.sendMessage(ChatColor.RED + args[3] + "isn't in the format x,y,z!");
                            if (pos2 == null) {
                                sender.sendMessage(ChatColor.RED + args[4] + "isn't in the format x,y,z!");
                            }
                            return true;
                        }
                        if (pos2 == null) {
                            sender.sendMessage(ChatColor.RED + args[4] + "isn't in the format x,y,z!");
                            return true;
                        }
                        ConfigUtils.addWell(plugin, new Well(args[2], new BoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(),
                                pos2.getX(), pos2.getY(), pos2.getZ()), Objects.requireNonNull(pos1.getWorld())));
                        sender.sendMessage(ChatColor.GREEN + "Successfully created the well (" + args[2] +")");
                        return true;
                    case "remove":
                        if (args.length > 3) {
                            showHelp(sender, label);
                            sender.sendMessage(MANY_ARGS);
                        }
                        if (args.length == 2) {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(MUST_BE_PLAYER);
                                return true;
                            }
                            final Player playerRemoval = (Player) sender;
                            final Set<Well> wellsToRemove = ConfigUtils.getWellsInLocation(ConfigUtils.getWells(plugin), playerRemoval.getLocation());
                            final int wellAmount = wellsToRemove.size();
                            sender.sendMessage(ChatColor.GOLD + "Removing " + ChatColor.BOLD + wellAmount + ChatColor.GOLD + " wells");
                            for (Well well : wellsToRemove) {
                                ConfigUtils.removeWell(plugin, well);
                            }
                            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + ChatColor.BOLD + wellAmount + ChatColor.GREEN + " wells!");
                            return true;
                        }
                        final Well wellToRemove = Well.getWellByName(ConfigUtils.getWells(plugin), args[2]);
                        sender.sendMessage(ChatColor.GOLD + "Removing " + ChatColor.BOLD + 1 + ChatColor.GOLD + " wells");
                        if (wellToRemove == null) {
                            sender.sendMessage(ChatColor.RED + args[2] + " isn't a valid well name!");
                            return true;
                        }
                        ConfigUtils.removeWell(plugin, wellToRemove);
                        sender.sendMessage(ChatColor.GREEN + "Successfully removed " + ChatColor.BOLD + 1 + ChatColor.GREEN + " wells!");
                        return true;
                    default:
                        showHelp(sender, label);
                        sender.sendMessage(INVALID_ARGUMENT);
                        return true;
                }
            default:
                showHelp(sender, label);
                sender.sendMessage(INVALID_ARGUMENT);
                return true;
        }
    }

    private void showHelp(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.AQUA + "BackupWell Command Usage");
        formatCMD(sender, label, "help", "Show this help page");
        formatCMD(sender, label, "reload", "Reload configuration files");
        formatCMD(sender, label, "well create <name> x,y,z x,y,z", "Create a well. Example: well create 10,40,7 1,50,20");
        formatCMD(sender, label, "well remove", "Remove all wells in your current location");
        formatCMD(sender, label, "well remove <name>", "Remove wells by its name");
        formatCMD(sender, label, "list", "Get the list of wells");
    }

    @Nullable
    private Location getLocationFromString(String coords, World world) {
        final String[] coordsArray = coords.split(",");
        if (coordsArray.length != 3) {
            return null;
        }
        try {
            final int x = Integer.parseInt(coordsArray[0]);
            final int y = Integer.parseInt(coordsArray[1]);
            final int z = Integer.parseInt(coordsArray[2]);
            return new Location(world, x, y, z);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void formatCMD(CommandSender sender, String cmd, String arg, String description) {
        sender.sendMessage(ChatColor.GOLD + "/" + cmd + " " + arg + " - " + ChatColor.AQUA + description);
    }
}
