package org.minermc.minerMCGlobalBoost.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.minermc.minerMCGlobalBoost.MinerMCGlobalBoost;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoostInventory {

    private final MinerMCGlobalBoost plugin;
    private final File boostsFile;
    private FileConfiguration boostsConfig;

    public BoostInventory(MinerMCGlobalBoost plugin) {
        this.plugin = plugin;
        this.boostsFile = new File(plugin.getDataFolder(), "boosts.yml");
        loadBoosts();
    }

    /**
     * Loads boost data from file
     */
    private void loadBoosts() {
        if (!boostsFile.exists()) {
            try {
                boostsFile.getParentFile().mkdirs();
                boostsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create boosts.yml file!");
                e.printStackTrace();
            }
        }
        boostsConfig = YamlConfiguration.loadConfiguration(boostsFile);
    }

    /**
     * Saves boost data to file
     */
    private void saveBoosts() {
        try {
            boostsConfig.save(boostsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save boosts.yml file!");
            e.printStackTrace();
        }
    }

    /**
     * Gives a boost to a player
     * @param playerUUID The player's UUID
     * @param multiplier The boost multiplier (2, 3, or 4)
     * @param durationMinutes The duration in minutes (30 or 60)
     */
    public void giveBoost(UUID playerUUID, int multiplier, int durationMinutes) {
        String boostKey = multiplier + "x_" + durationMinutes + "m";
        String path = playerUUID.toString() + "." + boostKey;

        int current = boostsConfig.getInt(path, 0);
        boostsConfig.set(path, current + 1);
        saveBoosts();
    }

    /**
     * Uses (consumes) a boost from a player's inventory
     * @param playerUUID The player's UUID
     * @param multiplier The boost multiplier (2, 3, or 4)
     * @param durationMinutes The duration in minutes (30 or 60)
     * @return true if the boost was consumed, false if they don't have it
     */
    public boolean useBoost(UUID playerUUID, int multiplier, int durationMinutes) {
        String boostKey = multiplier + "x_" + durationMinutes + "m";
        String path = playerUUID.toString() + "." + boostKey;

        int current = boostsConfig.getInt(path, 0);
        if (current > 0) {
            boostsConfig.set(path, current - 1);
            saveBoosts();
            return true;
        }
        return false;
    }

    /**
     * Gets the count of a specific boost type
     * @param playerUUID The player's UUID
     * @param multiplier The boost multiplier (2, 3, or 4)
     * @param durationMinutes The duration in minutes (30 or 60)
     * @return The number of boosts the player has
     */
    public int getBoostCount(UUID playerUUID, int multiplier, int durationMinutes) {
        String boostKey = multiplier + "x_" + durationMinutes + "m";
        String path = playerUUID.toString() + "." + boostKey;
        return boostsConfig.getInt(path, 0);
    }

    /**
     * Gets all boosts for a player
     * @param playerUUID The player's UUID
     * @return Map of boost type to count
     */
    public Map<String, Integer> getAllBoosts(UUID playerUUID) {
        Map<String, Integer> boosts = new HashMap<>();
        String uuidPath = playerUUID.toString();

        if (boostsConfig.contains(uuidPath)) {
            for (String key : boostsConfig.getConfigurationSection(uuidPath).getKeys(false)) {
                int count = boostsConfig.getInt(uuidPath + "." + key, 0);
                if (count > 0) {
                    boosts.put(key, count);
                }
            }
        }

        return boosts;
    }
}
