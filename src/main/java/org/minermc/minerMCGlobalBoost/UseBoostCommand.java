package org.minermc.minerMCGlobalBoost;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UseBoostCommand implements CommandExecutor, TabCompleter {

    private final MinerMCGlobalBoost plugin;

    public UseBoostCommand(MinerMCGlobalBoost plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Must be a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use boosts!");
            return true;
        }

        Player player = (Player) sender;

        // If no args, show inventory
        if (args.length == 0) {
            showInventory(player);
            return true;
        }

        // Require 2 arguments to use a boost
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /useboost <2x|3x|4x> <30m|1h>");
            sender.sendMessage(ChatColor.GRAY + "Or use /useboost to see your boosts");
            return true;
        }

        // Parse multiplier
        int multiplier;
        String boostType = args[0].toLowerCase();
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

        // Parse duration
        int durationMinutes;
        String duration = args[1].toLowerCase();
        switch (duration) {
            case "30m":
                durationMinutes = 30;
                break;
            case "1h":
                durationMinutes = 60;
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid duration! Use 30m or 1h");
                return true;
        }

        // Check if a different boost is already active
        int currentMultiplier = plugin.getBoostMultiplier();
        if (currentMultiplier > 1 && currentMultiplier != multiplier) {
            sender.sendMessage(ChatColor.RED + "A different boost (" + currentMultiplier + "x) is already active!");
            sender.sendMessage(ChatColor.RED + "Wait for it to expire, or use the same " + currentMultiplier + "x boost to add time.");
            return true;
        }

        // Check if player has the boost
        if (!plugin.getBoostInventory().useBoost(player.getUniqueId(), multiplier, durationMinutes)) {
            sender.sendMessage(ChatColor.RED + "You don't have a " + multiplier + "x boost for " + duration + "!");
            sender.sendMessage(ChatColor.GRAY + "Use /useboost to see your available boosts");
            return true;
        }

        // Activate the boost
        long durationSeconds = durationMinutes * 60L;
        plugin.startBoost(multiplier, durationSeconds, player.getName());

        String durationDisplay = durationMinutes == 60 ? "1 hour" : durationMinutes + " minutes";
        plugin.getServer().broadcastMessage(ChatColor.GOLD + "Global mining boost " + ChatColor.GREEN + multiplier + "x " +
                                          ChatColor.GOLD + "for " + ChatColor.GREEN + durationDisplay +
                                          ChatColor.GOLD + " activated by " + ChatColor.YELLOW + player.getName() + ChatColor.GOLD + "!");

        return true;
    }

    /**
     * Shows the player's boost inventory
     */
    private void showInventory(Player player) {
        Map<String, Integer> boosts = plugin.getBoostInventory().getAllBoosts(player.getUniqueId());

        if (boosts.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You don't have any boosts!");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "═══ " + ChatColor.YELLOW + "Your Boosts " + ChatColor.GOLD + "═══");
        for (Map.Entry<String, Integer> entry : boosts.entrySet()) {
            String boostKey = entry.getKey();
            int count = entry.getValue();

            // Format the boost key (e.g., "2x_30m" -> "2x for 30m")
            String[] parts = boostKey.split("_");
            player.sendMessage(ChatColor.GREEN + "  • " + ChatColor.YELLOW + parts[0] + ChatColor.GRAY + " for " + ChatColor.YELLOW + parts[1] + ChatColor.GRAY + " x" + count);
        }
        player.sendMessage(ChatColor.GRAY + "Use /useboost <type> <duration> to activate");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            // First argument: boost type
            List<String> completions = new ArrayList<>(Arrays.asList("2x", "3x", "4x"));
            String input = args[0].toLowerCase();
            completions.removeIf(s -> !s.startsWith(input));
            return completions;
        } else if (args.length == 2) {
            // Second argument: duration
            List<String> completions = new ArrayList<>(Arrays.asList("30m", "1h"));
            String input = args[1].toLowerCase();
            completions.removeIf(s -> !s.startsWith(input));
            return completions;
        }
        return new ArrayList<>();
    }
}
