package level.plugin;

import level.plugin.Events.PlayerJoinListener;
import level.plugin.Events.PlayerQuitListener;
import level.plugin.Exceptions.MYSQL.TableAlreadyExists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Main extends JavaPlugin {

    public static HashMap<Player, PlayerData> onlinePlayers = new HashMap<>();

    public void onEnable() {
        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    public void onDisable() {

    }

}
