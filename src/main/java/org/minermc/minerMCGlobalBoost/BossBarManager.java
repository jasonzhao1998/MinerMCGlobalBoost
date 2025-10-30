package org.minermc.minerMCGlobalBoost;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {

    private final MinerMCGlobalBoost plugin;
    private BossBar bossBar;

    public BossBarManager(MinerMCGlobalBoost plugin) {
        this.plugin = plugin;
    }

    /**
     * Shows or updates the boost boss bar with timer
     * @param multiplier The boost multiplier (1 = off, 2-4 = active boost)
     * @param activator The name of the player who activated the boost
     * @param timeRemaining Formatted time remaining (e.g., "29m 45s")
     * @param progress Progress value between 0.0 and 1.0
     */
    public void updateBoostBar(int multiplier, String activator, String timeRemaining, double progress) {
        if (multiplier <= 1) {
            // Remove boss bar if boost is off
            removeBoostBar();
            return;
        }

        String title = String.format("§6§l⛏ MINING DROPS §e%dx §6ACTIVE §a[%s]",
                                     multiplier, timeRemaining);

        if (bossBar == null) {
            // Create new boss bar (always green)
            bossBar = Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SOLID);
            bossBar.setProgress(progress);

            // Add all online players
            for (Player player : Bukkit.getOnlinePlayers()) {
                bossBar.addPlayer(player);
            }
        } else {
            // Update existing boss bar
            bossBar.setTitle(title);
            bossBar.setProgress(progress);
        }

        bossBar.setVisible(true);
    }

    /**
     * Shows or updates the boost boss bar without timer (for initial setup)
     * @param multiplier The boost multiplier (1 = off, 2-4 = active boost)
     * @param activator The name of the player who activated the boost
     */
    public void updateBoostBar(int multiplier, String activator) {
        updateBoostBar(multiplier, activator, "Starting...", 1.0);
    }

    /**
     * Removes the boost boss bar
     */
    public void removeBoostBar() {
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            bossBar = null;
        }
    }

    /**
     * Adds a player to the boss bar (called when a player joins)
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        if (bossBar != null && bossBar.isVisible()) {
            bossBar.addPlayer(player);
        }
    }

    /**
     * Removes a player from the boss bar (called when a player leaves)
     * @param player The player to remove
     */
    public void removePlayer(Player player) {
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    /**
     * Gets the current boss bar
     * @return The current boss bar, or null if none exists
     */
    public BossBar getBossBar() {
        return bossBar;
    }
}
