package org.minermc.minerMCGlobalBoost.task;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.minermc.minerMCGlobalBoost.MinerMCGlobalBoost;

public class BoostTimer extends BukkitRunnable {

    private final MinerMCGlobalBoost plugin;
    private long remainingSeconds;
    private final long totalSeconds;
    private final String activator;

    public BoostTimer(MinerMCGlobalBoost plugin, long durationSeconds, String activator) {
        this.plugin = plugin;
        this.remainingSeconds = durationSeconds;
        this.totalSeconds = durationSeconds;
        this.activator = activator;
    }

    /**
     * Adds more time to the existing timer
     * @param additionalSeconds Seconds to add
     */
    public void addTime(long additionalSeconds) {
        this.remainingSeconds += additionalSeconds;
    }

    /**
     * Gets remaining time in seconds
     */
    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    /**
     * Gets the progress as a value between 0.0 and 1.0
     */
    public double getProgress() {
        return Math.max(0.0, Math.min(1.0, (double) remainingSeconds / totalSeconds));
    }

    /**
     * Formats remaining time as a readable string
     */
    public String getFormattedTime() {
        long hours = remainingSeconds / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    @Override
    public void run() {
        remainingSeconds--;

        // Update boss bar every second
        if (remainingSeconds > 0) {
            plugin.getBossBarManager().updateBoostBar(
                plugin.getBoostMultiplier(),
                activator,
                getFormattedTime(),
                getProgress()
            );

            // Save to config every 10 seconds for persistence
            if (remainingSeconds % 10 == 0) {
                plugin.saveBoostToConfig(plugin.getBoostMultiplier(), activator, remainingSeconds);
            }
        } else {
            // Timer expired
            plugin.getServer().broadcastMessage(ChatColor.GOLD + "Global mining boost has " + ChatColor.RED + "expired" + ChatColor.GOLD + "!");
            plugin.saveBoostToConfig(1, activator, 0);
            plugin.getBossBarManager().removeBoostBar();
            this.cancel();
        }
    }

    /**
     * Starts the timer
     */
    public void start() {
        // Run every second (20 ticks)
        this.runTaskTimer(plugin, 20L, 20L);
    }
}
