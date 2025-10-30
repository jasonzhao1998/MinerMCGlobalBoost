package org.minermc.minerMCGlobalBoost;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinerMCGlobalBoost extends JavaPlugin {

    private int boostMultiplier = 1; // Default to 1x (no boost)
    private String boostActivator = "Server"; // Who activated the boost
    private BossBarManager bossBarManager;
    private BoostTimer boostTimer;
    private BoostInventory boostInventory;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Load boost state from config
        loadBoostFromConfig();

        // Initialize boss bar manager
        bossBarManager = new BossBarManager(this);

        // Initialize boost inventory
        boostInventory = new BoostInventory(this);

        // Restore timer if boost was active before restart
        long remainingSeconds = getConfig().getLong("boost-remaining-seconds", 0);
        if (boostMultiplier > 1 && remainingSeconds > 0) {
            boostTimer = new BoostTimer(this, remainingSeconds, boostActivator);
            boostTimer.start();
            bossBarManager.updateBoostBar(boostMultiplier, boostActivator);
            getLogger().info("Restored boost: " + boostMultiplier + "x (activated by " + boostActivator + ") - " + remainingSeconds + "s remaining");
        }

        // Register commands
        BoostCommand boostCommand = new BoostCommand(this);
        this.getCommand("boost").setExecutor(boostCommand);
        this.getCommand("boost").setTabCompleter(boostCommand);

        GiveBoostCommand giveBoostCommand = new GiveBoostCommand(this);
        this.getCommand("giveboost").setExecutor(giveBoostCommand);
        this.getCommand("giveboost").setTabCompleter(giveBoostCommand);

        UseBoostCommand useBoostCommand = new UseBoostCommand(this);
        this.getCommand("useboost").setExecutor(useBoostCommand);
        this.getCommand("useboost").setTabCompleter(useBoostCommand);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Startup message
        getLogger().info(ChatColor.GREEN + "MinerMCGlobalBoost has been enabled!");
        getLogger().info("Use /boost <2x|3x|4x|off> to control the global mining boost");
    }

    @Override
    public void onDisable() {
        // Cancel timer if running
        if (boostTimer != null) {
            boostTimer.cancel();
            // Save remaining time
            saveBoostToConfig(boostMultiplier, boostActivator, boostTimer.getRemainingSeconds());
        }

        // Clean up boss bar
        if (bossBarManager != null) {
            bossBarManager.removeBoostBar();
        }

        // Plugin shutdown logic
        getLogger().info(ChatColor.RED + "MinerMCGlobalBoost has been disabled!");
    }

    /**
     * Gets the current boost multiplier
     * @return The current boost multiplier (1 = no boost, 2 = 2x, etc.)
     */
    public int getBoostMultiplier() {
        return boostMultiplier;
    }

    /**
     * Sets the boost multiplier
     * @param multiplier The multiplier to set (1 = no boost, 2 = 2x, etc.)
     */
    public void setBoostMultiplier(int multiplier) {
        this.boostMultiplier = multiplier;
    }

    /**
     * Gets the boss bar manager
     * @return The boss bar manager
     */
    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    /**
     * Gets the boost inventory
     * @return The boost inventory
     */
    public BoostInventory getBoostInventory() {
        return boostInventory;
    }

    /**
     * Loads the boost state from config
     */
    private void loadBoostFromConfig() {
        boostMultiplier = getConfig().getInt("boost-multiplier", 1);
        boostActivator = getConfig().getString("boost-activator", "Server");
    }

    /**
     * Saves the boost state to config
     * @param multiplier The boost multiplier to save
     * @param activator The name of who activated the boost
     * @param remainingSeconds Remaining time in seconds
     */
    public void saveBoostToConfig(int multiplier, String activator, long remainingSeconds) {
        this.boostMultiplier = multiplier;
        this.boostActivator = activator;

        getConfig().set("boost-multiplier", multiplier);
        getConfig().set("boost-activator", activator);
        getConfig().set("boost-remaining-seconds", remainingSeconds);
        saveConfig();
    }

    /**
     * Starts a new boost timer or adds time to existing one
     * @param multiplier The boost multiplier
     * @param durationSeconds Duration in seconds
     * @param activator Who activated the boost
     */
    public void startBoost(int multiplier, long durationSeconds, String activator) {
        this.boostMultiplier = multiplier;

        if (boostTimer != null && boostTimer.getRemainingSeconds() > 0) {
            // Add time to existing timer
            boostTimer.addTime(durationSeconds);
        } else {
            // Cancel old timer if exists
            if (boostTimer != null) {
                boostTimer.cancel();
            }

            // Start new timer
            boostTimer = new BoostTimer(this, durationSeconds, activator);
            boostTimer.start();
        }

        // Save to config
        saveBoostToConfig(multiplier, activator, boostTimer.getRemainingSeconds());
    }

    /**
     * Stops the current boost
     */
    public void stopBoost() {
        if (boostTimer != null) {
            boostTimer.cancel();
            boostTimer = null;
        }
        this.boostMultiplier = 1;
        saveBoostToConfig(1, boostActivator, 0);
        bossBarManager.removeBoostBar();
    }

    /**
     * Formats a duration in seconds to a readable string
     */
    private String formatDuration(long seconds) {
        long minutes = seconds / 60;
        if (minutes >= 60) {
            return (minutes / 60) + " hour" + (minutes >= 120 ? "s" : "");
        }
        return minutes + " minute" + (minutes != 1 ? "s" : "");
    }
}
