package level.plugin;

import level.plugin.Commands.*;
import level.plugin.Errors.TheUserhasNotplayedBefore;
import level.plugin.Errors.TheUserisNotOnline;
import level.plugin.Events.*;
import level.plugin.Leaderboard.LeaderHeads;
import level.plugin.Leaderboard.LeaderboardHandler;
import level.plugin.Leaderboard.LeaderboardHologram;
import level.plugin.Libs.*;
import level.plugin.SupportedPluginsClasses.PlaceHolderAPI;
import level.plugin.SupportedPluginsClasses.Vault;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Main extends JavaPlugin {

    public static HashMap<Player, PlayerData> playerData = new HashMap<>(); //Allows easy access to players info!
    public static Lib lib;
    public static Scoreboard scoreboard = null; //For Teams on Top of head!

    //MYSQL VARIABLES
    private static Connection connection = null;
    static String host;
    static String database;
    static String username_login;
    static String password;
    static int port;
    public static Statement statement;

    public void onEnable() {
        Commands();
        Events();
        configHandler.handleConfigs();
        createHandlerQuests();
        LeaderboardHologram.SpawnLeaderboardHologram();
        if (LeaderHeads.UpdateSigns()) {
            LeaderHeads.UpdateSignHeadScheduler();
        }
        if (Bukkit.getOnlinePlayers().size() != 0) { //If a reload happens. It will load all the player's data.
            for (Player player : Bukkit.getOnlinePlayers()) {
                Main.playerData.put(player, new PlayerData(player));
            }
        }
        if (setupLib()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "This Server has full support of this plugin!");
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "This Server doesn't fully support this plugin. Will Try to run anyway!");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            // Check if PlaceholderAPI is installed.
            Bukkit.getConsoleSender().sendMessage("PlaceholderAPI found!");
            new PlaceHolderAPI(this).register();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabling the PlaceHolders! - Using PlaceHolder PlaceholderExpansion.");
        }
        if (Vault.isVaultInstalled()) {
            Vault.setupPermissions();
            Bukkit.getConsoleSender().sendMessage("Vault found!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Hooking into Vault!");
        }
    }

    private void Events() {
        getServer().getPluginManager().registerEvents(new OnChat(), this);
        getServer().getPluginManager().registerEvents(new OnJoin(), this);
        getServer().getPluginManager().registerEvents(new OnQuit(), this);
        getServer().getPluginManager().registerEvents(new OnBlockBreak(), this);
        getServer().getPluginManager().registerEvents(new onDeath(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandPreprocess(), this);
        getServer().getPluginManager().registerEvents(new SignChangeEvent(), this);
    }

    private void Commands() {
        getCommand("levelstats").setExecutor(new Stats());
        getCommand("AddPoints").setExecutor(new AddPoints());
        getCommand("ChangeLevel").setExecutor(new ChangeLevel());
        getCommand("DebugLevel").setExecutor(new DebugLevel());
        getCommand("AddLevel").setExecutor(new AddLevel());
        getCommand("ChangePoints").setExecutor(new ChangePoints());
        getCommand("levelleaderboard").setExecutor(new LevelLeaderboardCommand());
        getCommand("levelreload").setExecutor(new levelreload());
    }

    public void onDisable() {
        //onDisable is now not useless!
        LeaderboardHologram.RemoveleaderboardHologram();
    }


    public void createHandlerQuests() {
        if (Bukkit.getPluginManager().isPluginEnabled("Quests")) {
            File JarFolder = new File("plugins" + File.separator + "Quests", "modules");
            File Jar = new File(JarFolder, "Level_Quests_Handler.jar");
            if (!Jar.exists()) {
                this.saveResource("Level_Quests_Handler.jar", false);
                File file = new File(this.getDataFolder(), "Level_Quests_Handler.jar");
                try {
                    InputStream in = new FileInputStream(file);
                    OutputStream out = new FileOutputStream(Jar);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Quests Level Handler Installed!");
                    if (Bukkit.getPluginManager().isPluginEnabled("Quests")) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You need to restart the server for it to take effect!");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static boolean isPlayerOnline(String username) {
        Player player = Bukkit.getOnlinePlayers().stream().filter(player2 -> username.equalsIgnoreCase(player2.getName())).findAny().orElse(null);
        return player != null;
    }


    public static Player getPlayerbyString(String username) throws TheUserisNotOnline {
        if (Bukkit.getPlayer(username) != null) {
            return Bukkit.getPlayer(username);
        } else {
            throw (new TheUserisNotOnline("The User is not Online!"));
        }
    }

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayer(String username) throws TheUserhasNotplayedBefore {
        if (!Bukkit.getOfflinePlayer(username).hasPlayedBefore() || Bukkit.getOfflinePlayer(username) == null) {
            throw (new TheUserhasNotplayedBefore());
        } else {
            return Bukkit.getOfflinePlayer(username);
        }
    }

    private boolean setupLib() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }
        //getLogger().info("Your server is running version " + version);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Your Server is Version: " + version);
        if (version.equals("v1_8_R3")) {
            lib = new Lib1_8_R3();
        } else if (version.equals("v1_9_R1")) {
            lib = new Lib1_9_R1();
        } else if (version.equals("v1_12_R1")) {
            lib = new Lib1_12_R1();
        } else if (version.equals("v1_13_R1")) {
            lib = new Lib1_13_R1();
        } else if (version.equals("v1_8_R1")) {
            lib = new Lib1_8_R1();
        } else if (version.equals("v1_13_R2")) {
            lib = new Lib1_13_R2();
        } else if (version.equals("v1_8_R2")) {
            lib = new Lib1_8_R2();
        } else if (version.equals("v1_10_R1")) {
            lib = new Lib1_10_R1();
        }
        return lib != null;
    }

    public static void openConnectionMYSQL() throws SQLException, ClassNotFoundException {
        if (connection == null) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username_login, password);
            statement = connection.createStatement();
            //Checks a table if the table doesn't exist
            String sqlCreate = "CREATE TABLE IF NOT EXISTS PlayerData  (UUID VARCHAR(50), Level INTEGER, Points INTEGER);";
            statement.execute(sqlCreate);
        }

        if (connection.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username_login, password);
            statement = connection.createStatement();
            //Checks a table if the table doesn't exist
            String sqlCreate = "CREATE TABLE IF NOT EXISTS PlayerData  (UUID VARCHAR(50), Level INTEGER, Points INTEGER);";
            statement.execute(sqlCreate);
        }
    }


}
