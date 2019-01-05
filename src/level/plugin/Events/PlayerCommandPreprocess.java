package level.plugin.Events;

import level.plugin.Leaderboard.LeaderboardHandler;
import level.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlayerCommandPreprocess implements Listener {
    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] array = message.split(" ");
        if (array[0].equalsIgnoreCase("/stats")) {
            File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(Config);
            if (yml.getBoolean("redirectstatstolevelstats")) {
                String newcommand = message.replaceAll("stats", "levelstats");
                newcommand = newcommand.replaceAll("/", "");
                Bukkit.dispatchCommand(event.getPlayer(), newcommand);
                event.setCancelled(true);
            }
        }
        File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(Config);
        if (LeaderboardHandler.getEnabled()) {
            String command = LeaderboardHandler.getCommand();
            if (array[0].equalsIgnoreCase(command)) {
                event.getPlayer().sendMessage(LeaderboardHandler.GetLeaderboardString());
                event.setCancelled(true);
            }
        }
    }
}
