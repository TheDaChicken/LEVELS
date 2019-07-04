package level.plugin;

import level.plugin.Errors.MaxLevel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class PlayerPointsTimeHandler {

    public static void PlayerPointsTimeScheduler() {
        // Check points list.
        // Check if off the point list.
        int runnable = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Main.playerData.get(player) != null) {
                    PlayerData playerData = Main.playerData.get(player);
                    if (playerData.PLAYER_JOIN_MILLIS != 0) {
                        long PLAYER_JOIN_SECONDS = TimeUnit.MILLISECONDS.toSeconds(playerData.PLAYER_JOIN_MILLIS);
                        long CURRENT_TIME = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                        int last_seconds_difference = playerData.LAST_SECONDS_DIFFERENCE;
                        int seconds_difference = Math.toIntExact(CURRENT_TIME - PLAYER_JOIN_SECONDS);
                        if (seconds_difference != last_seconds_difference) {
                            playerData.LAST_SECONDS_DIFFERENCE = seconds_difference;
                            File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
                            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(Config);
                            // Check points list.
                            if (cfg.contains("PlayerTimeToPoints.PointsList." + seconds_difference)) {
                                int points = cfg.getInt("PlayerTimeToPoints.PointsList." + seconds_difference);
                                player.sendMessage(Messages.PlayerPointsToTimeGivenPoints(points, seconds_difference));
                                try {
                                    Main.playerData.get(player).Addpoints(points);
                                } catch (MaxLevel e) {
                                    player.sendMessage(Messages.AddPointsMaxLevelCatchMessage(player));
                                }
                            }
                        }
                    }
                }
            }
        }, 0L, 4L);
    }

}