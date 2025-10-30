package org.minermc.minerMCGlobalBoost;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BlockBreakListener implements Listener {

    private final MinerMCGlobalBoost plugin;

    public BlockBreakListener(MinerMCGlobalBoost plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        // Only apply boost if it's greater than 1x
        if (plugin.getBoostMultiplier() <= 1) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material blockType = block.getType();

        // Check if player is in creative mode
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // Check if the block is a minable material
        if (!isMinableMaterial(blockType)) {
            return;
        }

        // Get the tool used
        ItemStack tool = player.getInventory().getItemInMainHand();

        // Get the natural drops
        Collection<ItemStack> drops = block.getDrops(tool);

        if (drops.isEmpty()) {
            return;
        }

        // Calculate BONUS items (not total) to add on top of what DeluxeMines/other plugins give
        // For 2x boost, add 1x bonus (100% extra)
        // For 3x boost, add 2x bonus (200% extra)
        // For 4x boost, add 3x bonus (300% extra)
        int bonusMultiplier = plugin.getBoostMultiplier() - 1;

        // Add bonus items directly to player inventory
        for (ItemStack drop : drops) {
            ItemStack bonusItems = drop.clone();
            bonusItems.setAmount(drop.getAmount() * bonusMultiplier);

            // Add to player inventory, drop excess if inventory is full
            player.getInventory().addItem(bonusItems).forEach((index, excess) ->
                player.getWorld().dropItemNaturally(player.getLocation(), excess)
            );
        }
    }

    /**
     * Checks if the material is a minable resource using Bukkit's Tag system.
     * This uses Minecraft's built-in mineable tags, making it more maintainable
     * and automatically compatible with new blocks added in future versions.
     */
    private boolean isMinableMaterial(Material material) {
        // Check if the block is in any of the mineable tags
        return Tag.MINEABLE_AXE.isTagged(material) ||
               Tag.MINEABLE_PICKAXE.isTagged(material) ||
               Tag.MINEABLE_SHOVEL.isTagged(material) ||
               Tag.MINEABLE_HOE.isTagged(material);
    }
}
