package org.minermc.minerMCGlobalBoost;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final MinerMCGlobalBoost plugin;

    public PlayerJoinListener(MinerMCGlobalBoost plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Add player to boss bar if boost is active
        plugin.getBossBarManager().addPlayer(event.getPlayer());
    }
}
