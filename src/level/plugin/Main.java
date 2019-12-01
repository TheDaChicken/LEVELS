package level.plugin;

import level.plugin.Commands.*;
import level.plugin.Enums.LevelUpTypeOptions;
import level.plugin.Enums.StorageOptions;
import level.plugin.Events.LevelUpListener;
import level.plugin.Events.PlayerJoinListener;
import level.plugin.Events.PlayerQuitListener;
import level.plugin.Exceptions.Player.PlayerNameDoesntExist;
import level.plugin.Exceptions.Player.PlayerNotPlayedBefore;
import level.plugin.Libs.*;
import level.plugin.SupportedPluginsClasses.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Main extends CustomJavaPlugin {

    public static Lib lib = null;

    public static HashMap<Player, PlayerData> onlinePlayers = new HashMap<>();
    public static MySQL mySQL = null;

    public void onEnable() {
        // Register Commands
        getCommand("levelstats").setExecutor(new LevelStats());
        getCommand("changelevel").setExecutor(new ChangeLevel());
        getCommand("AddPoints").setExecutor(new AddPoints());
        getCommand("changepoints").setExecutor(new ChangePoints());
        getCommand("addLevel").setExecutor(new AddLevel());
        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new LevelUpListener(), this);
        setupConfig();
        setupMessages();
        if (setupLib()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "This Server has full support of this plugin!");
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "This Server doesn't fully support this plugin.");
        }
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
            if (!Main.onlinePlayers.containsKey(player)) {
                Main.onlinePlayers.put(player, new PlayerData(player));
            }
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

    private boolean setupLib() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Your Server is Version: " + version);
        switch (version) {
            case "v1_8_R3":
                lib = new Lib1_8_R3();
            case "v1_9_R1":
                lib = new Lib1_9_R1();
            case "v1_12_R1":
                lib = new Lib1_12_R1();
            case "v1_13_R1":
                lib = new Lib1_13_R1();
            case "v1_8_R1":
                lib = new Lib1_8_R1();
            case "v1_13_R2":
                lib = new Lib1_13_R2();
            case "v1_8_R2":
                lib = new Lib1_8_R2();
            case "v1_10_R1":
                lib = new Lib1_10_R1();
        }
        return lib != null;
    }

}
