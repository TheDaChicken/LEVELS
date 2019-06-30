package level.plugin.Events;

import level.plugin.Errors.MaxLevel;
import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class OnBlockBreak implements Listener {
    public OnBlockBreak(Main main) {
    }

    @EventHandler
    public void onBlock(BlockBreakEvent event) {
        File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Config);
        Player player = event.getPlayer();
        int pointstogive;

        if (player.getGameMode() != GameMode.CREATIVE) {
            if (yml.getBoolean("BlockBreaking")) {
                if (yml.getBoolean("BlockBreakingRandom")) {
                    int min = yml.getInt("BlockBreakingMin");
                    int max = yml.getInt("BlockBreakingMax");
                    pointstogive = ThreadLocalRandom.current().nextInt(min, max + 1);
                } else {
                    pointstogive = yml.getInt("BlockBreakingPoints");
                }
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
