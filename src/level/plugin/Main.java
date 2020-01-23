package level.plugin;

import level.plugin.Commands.*;
import level.plugin.Enums.LevelUpTypeOptions;
import level.plugin.Enums.StorageOptions;
import level.plugin.Events.*;
import level.plugin.Exceptions.Player.PlayerNameDoesntExist;
import level.plugin.Exceptions.Player.PlayerNotPlayedBefore;
import level.plugin.Libs.*;
import level.plugin.SupportedPluginsClasses.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends CustomJavaPlugin {

    public static Scoreboard scoreboard = null;
    public static Lib lib = null;

    public static HashMap<Player, PlayerData> onlinePlayers = new HashMap<>();
    public static MySQL mySQL = null;

    public static String translateColorChat(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
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

    public static PlayerData getPlayerData(Player player) {
        if (!Main.onlinePlayers.containsKey(player)) {
            Main.onlinePlayers.put(player, new PlayerData(player));
        }
        return Main.onlinePlayers.get(player);
    }

    private static Set<Class<?>> getAllExtendedOrImplementedTypesRecursively(Class<?> clazz) {
        // FOUND THIS SOMEWHERE ON THE INTERNET SORRY.
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
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        setupConfig();
        setupMessages();
        if (setupLib()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "This Server has full support of this plugin!");
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "This Server doesn't fully support this plugin.");
        }
        SupportedPlugins.setupSupportedPlugins();
        if (!SupportedPlugins.isNameTagEditInstalled()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
    }

    private void setupMessages() {
        this.getMessageFile();
        this.saveDefaultMessages();
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
        if (yml.getBoolean("KillEntities.EnableMobsPoints")) {
            setupMobListConfig();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "StoragePlace: " +
                StorageOptions.getStorageOption().name() + ".");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "LevelUpType: " +
                LevelUpTypeOptions.getLevelUpType().name() + ".");
    }

    private void setupMobListConfig() {
        File mob_list_config = new File(this.getDataFolder().getPath(), "moblistconfig.yml");
        if (!mob_list_config.exists()) {
            try {
                if (mob_list_config.createNewFile()) {
                    YamlConfiguration moblistconfig_cfg = YamlConfiguration.loadConfiguration(mob_list_config);
                    moblistconfig_cfg.set("Info", "This is the config that allows you to set the points given to a person when mob is killed.\n" +
                            "You can remove any of this mobs from the list if you don't want people given points for the mob.");
                    for (EntityType entityType : EntityType.values()) {
                        Class<? extends Entity> EntityClass = entityType.getEntityClass();
                        if (EntityClass != null) {
                            if (getAllExtendedOrImplementedTypesRecursively(EntityClass).contains(LivingEntity.class)) {
                                try {
                                    moblistconfig_cfg.set("mobs." + entityType.getName().toUpperCase(), 5);
                                } catch (NullPointerException ignored) {

                                }
                            }
                        }
                    }
                    moblistconfig_cfg.save(mob_list_config);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                return true;
            case "v1_9_R1":
                lib = new Lib1_9_R1();
                return true;
            case "v1_12_R1":
                lib = new Lib1_12_R1();
                return true;
            case "v1_13_R1":
                lib = new Lib1_13_R1();
                return true;
            case "v1_8_R1":
                lib = new Lib1_8_R1();
                return true;
            case "v1_13_R2":
                lib = new Lib1_13_R2();
                return true;
            case "v1_8_R2":
                lib = new Lib1_8_R2();
                return true;
            case "v1_10_R1":
                lib = new Lib1_10_R1();
                return true;
            case "v1_15_R1":
                lib = new Lib1_15_R1();
                return true;
            case "v1_14_R1":
                lib = new Lib1_14_R1();
                return true;
        }
        return false;
    }

}
