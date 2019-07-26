package level.plugin.Events;

import level.plugin.Errors.MaxLevel;
import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class OnBlockBreak implements Listener {
    @EventHandler
    public void onBlock(BlockBreakEvent event) {
        File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Config);
        Player player = event.getPlayer();
        int pointstogive = -1;

        if (player.getGameMode() != GameMode.CREATIVE) {
            String ways = yml.getString("BlockBreakingWaysGivingPoints");
            if (ways.equalsIgnoreCase("RANDOM")) {
                int min = yml.getInt("BlockBreakingMin");
                int max = yml.getInt("BlockBreakingMax");
                pointstogive = ThreadLocalRandom.current().nextInt(min, max + 1);
            } else if (ways.equalsIgnoreCase("SPECIFIC")) {
                File blocklistconfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "blocklistconfig.yml");
                if (blocklistconfig.exists()) {
                    Block block = event.getBlock();
                    String blockName = block.getType().name();
                    YamlConfiguration blocklist_cfg = YamlConfiguration.loadConfiguration(blocklistconfig);
                    boolean isThere = blocklist_cfg.contains("material." + blockName);
                    if (isThere) {
                        pointstogive = blocklist_cfg.getInt("material." + blockName);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Unable to find blocklistconfig.yml.\nPlease restart server.");
                    return;
                }
            } else {
                pointstogive = yml.getInt("BlockBreakingPoints");
            }

            if (pointstogive != -1) {
                if (Main.playerData.get(player) == null) {
                    Main.playerData.put(player, new PlayerData(player));
                }
                try {
                    Main.playerData.get(player).Addpoints(pointstogive);
                } catch (MaxLevel e) {
                    player.sendMessage(Messages.AddPointsMaxLevelCatchMessage(player));
                }
            }
        }
    }

}
