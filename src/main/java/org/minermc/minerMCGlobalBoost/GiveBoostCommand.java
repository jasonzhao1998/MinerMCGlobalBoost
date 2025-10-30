package org.minermc.minerMCGlobalBoost;

import org.bukkit.Bukkit;
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
import java.util.stream.Collectors;

public class GiveBoostCommand implements CommandExecutor, TabCompleter {

    private final MinerMCGlobalBoost plugin;

    public GiveBoostCommand(MinerMCGlobalBoost plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /giveboost <player> <2x|3x|4x> <30m|1h>");
            return true;
        }

        // Get target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        // Parse multiplier
        int multiplier;
        String boostType = args[1].toLowerCase();
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
        String duration = args[2].toLowerCase();
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

        // Give the boost
        plugin.getBoostInventory().giveBoost(target.getUniqueId(), multiplier, durationMinutes);

        // Send messages
        sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " a " + multiplier + "x boost for " + duration + "!");
        target.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + multiplier + "x boost " + ChatColor.GREEN + "for " + ChatColor.GOLD + duration + ChatColor.GREEN + "!");
        target.sendMessage(ChatColor.GRAY + "Use /useboost " + multiplier + "x " + duration + " to activate it");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            // First argument: player names
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            // Second argument: boost type
            List<String> completions = new ArrayList<>(Arrays.asList("2x", "3x", "4x"));
            String input = args[1].toLowerCase();
            completions.removeIf(s -> !s.startsWith(input));
            return completions;
        } else if (args.length == 3) {
            // Third argument: duration
            List<String> completions = new ArrayList<>(Arrays.asList("30m", "1h"));
            String input = args[2].toLowerCase();
            completions.removeIf(s -> !s.startsWith(input));
            return completions;
        }
        return new ArrayList<>();
    }
}
