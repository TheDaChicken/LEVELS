package level.plugin;

import level.plugin.Events.PlayerJoinListener;
import level.plugin.Events.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Main extends JavaPlugin {

    public static HashMap<Player, PlayerData> onlinePlayers = new HashMap<>();
    public static MySQL mySQL = null;

    public void onEnable() {
        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        setupConfig();
    }

    public void onDisable() {

    }

    private void setupConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        FileConfiguration yml = this.getConfig();
        String storagePlace = yml.getString("StoragePlace");
        if (!StorageOptions.parseStorage(storagePlace)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "UNKNOWN STORAGE TYPE - Auto choosing Storage TYPE: FILE");
            StorageOptions.setStorageOption(StorageOptions.FILE);
        }
        if (StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
            String host = yml.getString("MYSQLOptions.Host");
            int port = yml.getInt("MYSQLOptions.Port");
            String database = yml.getString("MYSQLOptions.Database");
            String username = yml.getString("MYSQLOptions.Username");
            String password = yml.getString("MYSQLOptions.Password");
            MySQL mySQL = new MySQL(host, database, username, password, port);
            if (!mySQL.openConnection()) {
                Bukkit.getConsoleSender().sendMessage("Failed to connect to MYSQL Server.");
            }
        }
    }

}
