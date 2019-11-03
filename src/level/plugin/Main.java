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
        MySQL mySQL = new MySQL("localhost", "test", "Bukkit", "Zxcv9998", 3306);
        if (mySQL.openConnection()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("UUID", "VARCHAR(50)");
            hashMap.put("Level", "INTEGER");
            hashMap.put("Points", "INTEGER");
            if (mySQL.createTableIfNotExist("PlayerData", hashMap)) {
                Bukkit.broadcastMessage("asdasd");
            }
        }
    }

    public void onDisable() {

    }

}