package me.dkim19375.backupwell.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompletionHandler implements TabCompleter {

    private final HashMultimap<String, String> completesListMap;

    public TabCompletionHandler() {
        completesListMap = HashMultimap.create();
        add("core", "help", "well", "reload");
        add("well", "create", "list", "remove");
    }

    private void add(@SuppressWarnings("SameParameterValue") String key, String... args) { completesListMap.putAll(key, Arrays.asList(args)); }

    private List<String> getPartial(String token, Iterable<String> collection) {
        return StringUtil.copyPartialMatches(token, collection, new ArrayList<>());
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        switch (args.length) {
            case 0:
                return Lists.newArrayList(completesListMap.get("core"));
            case 1: return getPartial(args[0], completesListMap.get("core"));
            case 2:
                if (args[0].equalsIgnoreCase("well")) {
                    return getPartial(args[1], completesListMap.get("well"));
                }
            default:
                return ImmutableList.of();
        }
    }
}
