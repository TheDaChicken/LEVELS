package level.plugin;

import level.plugin.Commands.AddPoints;
import level.plugin.Commands.ChangeLevel;
import level.plugin.Commands.LevelStats;
import level.plugin.Enums.LevelUpTypeOptions;
import level.plugin.Enums.StorageOptions;
import level.plugin.Events.LevelUpListener;
import level.plugin.Events.PlayerJoinListener;
import level.plugin.Events.PlayerQuitListener;
import level.plugin.Exceptions.Player.PlayerNameDoesntExist;
import level.plugin.Exceptions.Player.PlayerNotPlayedBefore;
import level.plugin.SupportedPluginsClasses.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Main extends CustomJavaPlugin {

    public static HashMap<Player, PlayerData> onlinePlayers = new HashMap<>();
    public static MySQL mySQL = null;

    public void onEnable() {
        // Register Commands
        getCommand("levelstats").setExecutor(new LevelStats());
        getCommand("changelevel").setExecutor(new ChangeLevel());
        getCommand("AddPoints").setExecutor(new AddPoints());
        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new LevelUpListener(), this);
        setupConfig();
        setupMessages();
        SupportedPlugins.setupSupportedPlugins();
    }

    public void onDisable() {

    }

    public static Integer convertStringToInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static PlayerData getPlayerData(String username) throws PlayerNameDoesntExist, PlayerNotPlayedBefore {
        Player player = Bukkit.getPlayer(username);
        if (player != null) {
            return Main.onlinePlayers.get(player);
        } else {
            return new PlayerData(username);
        }
    }

    private void setupConfig() {
        this.saveDefaultConfig();

        FileConfiguration yml = this.getConfig();
        String storagePlace = yml.getString("StoragePlace");
        String LevelUpType = yml.getString("LevelUpType");
        if (!StorageOptions.parseStorage(storagePlace)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "UNKNOWN STORAGE TYPE - Auto choosing Storage TYPE: FILE");
            StorageOptions.setStorageOption(StorageOptions.FILE);
        }
        if (!LevelUpTypeOptions.parseLevelUpType(LevelUpType)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "UNKNOWN LEVEL UP TYPE - Auto choosing Storage TYPE: SPECIFIC");
            LevelUpTypeOptions.setLevelUpType(LevelUpTypeOptions.SPECIFIC);
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
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "StoragePlace: " +
                StorageOptions.getStorageOption().name() + ".");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "LevelUpType: " +
                LevelUpTypeOptions.getLevelUpType().name() + ".");
    }

    private void setupMessages() {
        this.getMessageFile();
        this.saveDefaultMessages();
    }

}
