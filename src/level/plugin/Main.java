package level.plugin;

import level.plugin.Commands.*;
import level.plugin.Errors.TheUserhasNotplayedBefore;
import level.plugin.Errors.TheUserisNotOnline;
import level.plugin.Events.*;
import level.plugin.Leaderboard.LeaderHeads;
import level.plugin.Leaderboard.LeaderboardHologram;
import level.plugin.Libs.*;
import level.plugin.SupportedPluginsClasses.PlaceHolderAPI;
import level.plugin.SupportedPluginsClasses.Vault;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
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
    private static String host, database, username_login, password;
    private static int port;
    public static Statement statement;

    public void onEnable() {
        Commands();
        Events();
        Config();
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
            new PlaceHolderAPI(this).hook();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabling the PlaceHolders! - Using PlaceHolder EZPlaceholderHook.");
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
    }

    public void onDisable() {
        //onDisable is now not useless!
        LeaderboardHologram.RemoveleaderboardHologram();
    }

    private void Config() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        createLevelConfig();
        createModsListConfig();
        createMessageConfig();
        try {
            createHandlerQuests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Set<Class<?>> getAllExtendedOrImplementedTypesRecursively(Class<?> clazz) {
        List<Class<?>> res = new ArrayList<>();

        do {
            res.add(clazz);

            // First, add all the interfaces implemented by this class
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                res.addAll(Arrays.asList(interfaces));

                for (Class<?> interface_ : interfaces) {
                    res.addAll(getAllExtendedOrImplementedTypesRecursively(interface_));
                }
            }

            // Add the super class
            Class<?> superClass = clazz.getSuperclass();

            // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
            if (superClass == null) {
                break;
            }

            // Now inspect the superclass
            clazz = superClass;
        } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

        return new HashSet<Class<?>>(res);
    }

    public void createModsListConfig() {
        File l = new File(this.getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(l);
        if (yml.getBoolean("EnableKillMobsPoints")) {
            File Config = new File(this.getDataFolder().getPath(), "moblistconfig.yml");
            YamlConfiguration Configcfg = YamlConfiguration.loadConfiguration(Config);
            if (!Config.exists()) {
                try {
                    Config.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                Configcfg.set("Info", "This is the config that allows you to set the points given to a person when mob is killed.\n" +
                        "You can remove any of this mobs from the list if you don't want people given points for the mob.");
                for (EntityType entityType : EntityType.values()) {
                    //Only get Living Entity.
                    Class<? extends Entity> EntityClass = entityType.getEntityClass();
                    if (EntityClass != null) {
                        if (getAllExtendedOrImplementedTypesRecursively(EntityClass).contains(LivingEntity.class)) {
                            Configcfg.set("mobs." + entityType.getName(), 1);
                        }
                    }
                }
            }
            try {
                Configcfg.save(Config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void createLevelConfig() {
        File Config = new File(this.getDataFolder().getPath(), "levelsconfig.yml");
        if (!Config.exists()) {
            this.saveResource("levelsconfig.yml", false);
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Config);

        if (yml.getString("STORAGEPlace") != null) {
            if (yml.getString("STORAGEPlace").equalsIgnoreCase("FILE")) {
                StorageOptions.setStorageOption(StorageOptions.FILE);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "STORAGE PLACE: FILE");
            } else {
                if (yml.getString("STORAGEPlace").equalsIgnoreCase("MYSQL")) {
                    StorageOptions.setStorageOption(StorageOptions.MYSQL);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "STORAGE PLACE: MYSQL");
                } else {
                    StorageOptions.setStorageOption(StorageOptions.FILE);
                    Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "UNKNOWN STORAGE TYPE - Auto choosing Storage TYPE: FILE");
                }
            }
        } else {
            StorageOptions.setStorageOption(StorageOptions.FILE);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "FOUND OLD CONFIG FROM UPDATE V2 OR LOWER, - Auto choosing Storage TYPE: FILE");
        }

        //SETTING MYSQL SETTINGS IN CLASS. (FOR NEW METHODS)
        host = yml.getString("MYSQLOptions.Host");
        port = yml.getInt("MYSQLOptions.Port");
        database = yml.getString("MYSQLOptions.Database");
        username_login = yml.getString("MYSQLOptions.Username");
        password = yml.getString("MYSQLOptions.Password");

        if (yml.getBoolean("EnableLevelOnTopOfHead")) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
    }

    public static HashMap<String, String> messageyml = new HashMap<>(); //Message YML VALUES AND KEYS. :/

    public void createMessageConfig() {
        File Config = new File(this.getDataFolder().getPath(), "messages.yml");
        if (!Config.exists()) {
            this.saveResource("messages.yml", false);
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Config);
        LoadMessageDefaultValues();

        boolean isMissingKeyMessage = false;

        for (String string : messageyml.keySet()) {
            if (yml.getString(string) == null) {
                if (!isMissingKeyMessage) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Found a value(s) that should be in the messages.yml that has been removed or added with a update! " +
                            "To keep the plugin working, it has been added to the messages.yml!");
                    isMissingKeyMessage = true;
                }
                if (messageyml.get(string).contains(">SPLIT>")) {
                    List<String> myList = new ArrayList<String>(Arrays.asList(messageyml.get(string).split(">SPLIT>")));
                    yml.set(string, myList);
                } else {
                    yml.set(string, messageyml.get(string));
                }
            }
        }
        if (isMissingKeyMessage) {
            try {
                yml.save(Config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void LoadMessageDefaultValues() {
        messageyml.put("AddPointsMaxLevelCatchMessage", "&c&lYour were giving points but was discarded because your on the highest level!");
        messageyml.put("PlayerhasNotJoinedServerBefore", "&c&lThat User has not joined this server before.");
        messageyml.put("ProblemAddingPoints", "&c&lThere was a problem adding points. Make sure, it's a number.");
        messageyml.put("ProblemAddingLevel", "&c&lThere was a problem adding Levels. Make sure, it's a number.");
        messageyml.put("AddLevelMaxLevelCatchMessage", "&c&lYour were giving levels but was discarded because your on the highest level!");
        messageyml.put("AddLevelUsage", "&c&lUsage: /addlevel playername number");
        messageyml.put("AddPointsUsage", "&c&lUsage: /addpoints playername number");
        messageyml.put("YouNeedOP", "&c&lYou don't have permissions to perform this command!");
        messageyml.put("LevelUpActionbar", "Level Up! %number%!");
        messageyml.put("LevelHasSet", "&a&lThe Level has been set!");
        messageyml.put("StatsInfoOnlySelf", "&b&lYour Level! > %levelprefix%%levelnumber%>SPLIT>&7&lPoints: %points%/%maxpoints%");
        messageyml.put("StatsInfoOnlySelfMaxLevel", "&b&lYour Level! > %levelprefix%%levelnumber% &7&l(Max Level)>SPLIT>&7&lPoints:%points%/%maxpoints%");
        messageyml.put("ChangePointsUsage", "&a&lUsage: /changepoints playername number");
        messageyml.put("CantAddThatManyPoints", "&a&lYou can't change the points that much! Please use /addpoints !");
        messageyml.put("ChangePointsMaxLevelCatchMessage", "&a&lYour points were changed but was discarded because your on the highest level!");
        messageyml.put("ChangeLevelUsage", "&a&lUsage: /changelevel playername number");
        messageyml.put("ProblemChangingLevel", "&a&lThere was a problem changing the level. Make sure, it's a number.");
        messageyml.put("LevelHigherThenMaxLevel", "&a&lThat Number is higher than the Max Level and cannot be set!");
        messageyml.put("StatsInfoPlayers", "&bLevel %player_name%&l > %levelprefix%%levelnumber%>SPLIT>&7&lPoints: %points%/%maxpoints%");
        messageyml.put("StatsInfoPlayersMaxLevel", "&bLevel %player_name%&l > %levelprefix%%levelnumber% &7&l(Max Level)>SPLIT>&7&lPoints: %points%/%maxpoints%");
        messageyml.put("AddPointsMessage", "&a&l+%amountofpoints% points");
        messageyml.put("MYSQLNotenabledinConfig", "&a&lMYSQL is disabled in the config. &7&lIf you want to use MYSQL please enable it in the config.");
        messageyml.put("ProblemwithMYSQLServer", "&c&lThere was a problem with the MYSQL Server.");
        messageyml.put("LevelLeaderboardCommandUsage", "&a&lUsage:>SPLIT>/levelleaderboard spawnHologram>SPLIT>/levelleaderboard deletehologram>SPLIT>/levelleaderboard setupleaderheads");
        messageyml.put("DeleteHologram", "&a&lRemoving and Deleting the Hologram!");
        messageyml.put("HologramAlreadyDeleted", "&c&lThe Hologram is already deleted.");
        messageyml.put("YouNeedtoBePlayer", "&c&lYou need to be a player for this command!");
        messageyml.put("ChangeLevelsMaxLevelCatchMessage", "&a&lYour Level were changed but was discarded because your on the highest level!");
        messageyml.put("LeaderHeadsLabelSign", "&lFirst Create a Sign (Make sure to label the %position% as the position and so on) and type this: >SPLIT>" +
                "[LEVEL]>SPLIT>" +
                "%pos%>SPLIT>" +
                "You can change the message that the sign creates in the levelsconfig.yml>SPLIT>" +
                "Next type /ll sethead <levelposition> to set the head location.");
        messageyml.put("leaderHeadNotANumber", "&c&l%pos% is not a number!");
        messageyml.put("LeaderHeadSignCreated", "&a&l#%pos% sign has been created!");
        messageyml.put("leaderboardSetHeadUsage", "&a&lUsage: /ll sethead <levelposition>");
        messageyml.put("leaderboardSetHeadNotNumber", "&a&l%number% is not a number! It needs to be number!");
        messageyml.put("PlaceHead", "&a&lNow Place a Skull in Location you have set!");
        messageyml.put("StoragePlaceNotWorking", "&c&lThere was a problem saving that. Please tell server owner that this has occurred.");
    }


    public void createHandlerQuests() throws IOException {
        if (Bukkit.getPluginManager().isPluginEnabled("Quests")) {
            File JarFolder = new File("plugins" + File.separator + "Quests", "modules");
            File Jar = new File(JarFolder, "Level_Quests_Handler.jar");
            if (!Jar.exists()) {
                this.saveResource("Level_Quests_Handler.jar", false);
                File file = new File(this.getDataFolder(), "Level_Quests_Handler.jar");
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
