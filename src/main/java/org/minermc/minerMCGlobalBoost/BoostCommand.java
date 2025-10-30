package org.minermc.minerMCGlobalBoost;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoostCommand implements CommandExecutor, TabCompleter {

    private final MinerMCGlobalBoost plugin;

    public BoostCommand(MinerMCGlobalBoost plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Handle /boost off
        if (args.length == 1 && args[0].equalsIgnoreCase("off")) {
            plugin.stopBoost();
            plugin.getServer().broadcastMessage(ChatColor.GOLD + "Global mining boost has been " + ChatColor.RED + "disabled" + ChatColor.GOLD + "!");
            return true;
        }

        // Require 2 arguments for activating boost
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /boost <2x|3x|4x> <minutes> OR /boost off");
            return true;
        }

        String boostType = args[0].toLowerCase();
        String activatorName = sender.getName();

        // Parse multiplier
        int multiplier;
        switch (boostType) {
            case "2x":
                multiplier = 2;
                break;
            case "3x":
                multiplier = 3;
                break;
            case "4x":
                multiplier = 4;
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid boost type! Use 2x, 3x, or 4x");
                return true;
        }

        // Parse duration (minutes)
        int minutes;
        try {
            minutes = Integer.parseInt(args[1]);
            if (minutes <= 0) {
                sender.sendMessage(ChatColor.RED + "Duration must be a positive number of minutes!");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid duration! Please enter a number of minutes (e.g., 30, 60)");
            return true;
        }

        long durationSeconds = minutes * 60L;
        String durationDisplay;
        if (minutes >= 60 && minutes % 60 == 0) {
            int hours = minutes / 60;
            durationDisplay = hours + " hour" + (hours > 1 ? "s" : "");
        } else if (minutes >= 60) {
            int hours = minutes / 60;
            int remainingMins = minutes % 60;
            durationDisplay = hours + "h " + remainingMins + "m";
        } else {
            durationDisplay = minutes + " minute" + (minutes > 1 ? "s" : "");
        }

        // Check if a different boost is already active
        int currentMultiplier = plugin.getBoostMultiplier();
        if (currentMultiplier > 1 && currentMultiplier != multiplier) {
            sender.sendMessage(ChatColor.RED + "A different boost (" + currentMultiplier + "x) is already active!");
            sender.sendMessage(ChatColor.RED + "Use /boost off first, or activate the same " + currentMultiplier + "x boost to add time.");
            return true;
        }

        // Start the boost
        boolean isAddingTime = (currentMultiplier == multiplier && currentMultiplier > 1);
        plugin.startBoost(multiplier, durationSeconds, activatorName);


        plugin.getServer().broadcastMessage(ChatColor.GOLD + "Global mining boost " + ChatColor.GREEN + multiplier + "x " +
                ChatColor.GOLD + "for " + ChatColor.GREEN + durationDisplay +
                ChatColor.GOLD + " activated by " + ChatColor.YELLOW + activatorName + ChatColor.GOLD + "!");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            // First argument: boost type
            List<String> completions = new ArrayList<>(Arrays.asList("2x", "3x", "4x", "off"));
            String input = args[0].toLowerCase();
            completions.removeIf(s -> !s.startsWith(input));
            return completions;
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("off")) {
            // Second argument: suggest common minute values
            List<String> completions = new ArrayList<>(Arrays.asList("30", "60", "90", "120"));
            String input = args[1];
            completions.removeIf(s -> !s.startsWith(input));
            return completions;
        }
        return new ArrayList<>();
    }
}
