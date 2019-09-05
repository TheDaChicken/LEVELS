package level.plugin;

import level.plugin.Leaderboard.LeaderboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class configHandler {

    private static JavaPlugin plugin = JavaPlugin.getPlugin(Main.class);

    private static File levels_config_file;
    private static YamlConfiguration levelConfig;

    public static void refreshLevelConfigCache() {

    }

    static void handleConfigs() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        levels_config_file = new File(plugin.getDataFolder().getPath(), "levelsconfig.yml");
        if (!levels_config_file.exists()) {
            plugin.saveResource("levelsconfig.yml", false);
        }
        levelConfig = YamlConfiguration.loadConfiguration(levels_config_file);

        //HANDLE STORAGE PLACE
        if (levelConfig.contains("STORAGEPlace")) {
            String storagePlace = levelConfig.getString("STORAGEPlace");
            if (storagePlace.equalsIgnoreCase("FILE")) {
                StorageOptions.setStorageOption(StorageOptions.FILE);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "STORAGE PLACE: FILE");
            } else {
                if (storagePlace.equalsIgnoreCase("MYSQL")) {
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

        if (levelConfig.contains("PlayerTimeToPoints.Enable")) {
            if (levelConfig.getBoolean("PlayerTimeToPoints.Enable")) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Starting PlayerTimeToPoints Scheduler.");
                PlayerPointsTimeHandler.PlayerPointsTimeScheduler();
            }
        }

        if (levelConfig.contains("Leaderboard.Add-1-line-leaderboard")) {
            if (levelConfig.getBoolean("Leaderboard.Add-1-line-leaderboard")) {
                LeaderboardHandler.one_lined_leader_board = true;
            }
        }

        //SETTING MYSQL SETTINGS IN CLASS. (FOR NEW METHODS)
        Main.host = levelConfig.getString("MYSQLOptions.Host");
        Main.port = levelConfig.getInt("MYSQLOptions.Port");
        Main.database = levelConfig.getString("MYSQLOptions.Database");
        Main.username_login = levelConfig.getString("MYSQLOptions.Username");
        Main.password = levelConfig.getString("MYSQLOptions.Password");

        if (levelConfig.getBoolean("EnableLevelOnTopOfHead")) {
            Main.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        // HANDLE moblistconfig.yml
        if (levelConfig.getBoolean("EnableKillMobsPoints")) {
            File mob_list_config = new File(plugin.getDataFolder().getPath(), "moblistconfig.yml");
            if (!mob_list_config.exists()) {
                try {
                    mob_list_config.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                YamlConfiguration moblistconfig_cfg = YamlConfiguration.loadConfiguration(mob_list_config);
                moblistconfig_cfg.set("Info", "This is the config that allows you to set the points given to a person when mob is killed.\n" +
                        "You can remove any of this mobs from the list if you don't want people given points for the mob.");
                for (EntityType entityType : EntityType.values()) {
                    //Only get Living Entity.
                    Class<? extends Entity> EntityClass = entityType.getEntityClass();
                    if (EntityClass != null) {
                        if (getAllExtendedOrImplementedTypesRecursively(EntityClass).contains(LivingEntity.class)) {
                            try {
                                moblistconfig_cfg.set("mobs." + entityType.getName().toUpperCase(), 1);
                            } catch (NullPointerException ignored) {

                            }
                        }
                    }
                }
                try {
                    moblistconfig_cfg.save(mob_list_config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //HANDLE BlocklistConfig.

        if (levelConfig.contains("BlockBreakingWaysGivingPoints")) {
            if (levelConfig.getString("BlockBreakingWaysGivingPoints").equalsIgnoreCase("SPECIFIC")) {
                File block_list_config = new File(plugin.getDataFolder().getPath(), "blocklistconfig.yml");
                if (!block_list_config.exists()) {
                    try {
                        block_list_config.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    YamlConfiguration block_list_cfg = YamlConfiguration.loadConfiguration(block_list_config);
                    block_list_cfg.set("Info", "This is the config that allows you to set the points given to a person when blocks are broken.\n" +
                            "You can remove any of this blcoks from the list if you don't want people given points for that block.");
                    for (Material entityType : Material.values()) {
                        if (entityType.isBlock()) {
                            block_list_cfg.set("material." + entityType.name(), 1);
                        }
                    }
                    try {
                        block_list_cfg.save(block_list_config);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        createMessageConfig();
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

    private static void createMessageConfig() {
        File Config = new File(plugin.getDataFolder().getPath(), "messages.yml");
        if (!Config.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Config);
        HashMap<String, String> messageyml = LoadMessageDefaultValues();

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

    private static HashMap<String, String> LoadMessageDefaultValues() {
        HashMap<String, String> messageyml = new HashMap<>(); //Message YML VALUES AND KEYS. :/
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
        messageyml.put("PlayerPointsToTimeGivenPoints", "&a&lYou were given %amountofpoints% points for playing on this server for %amountofseconds% seconds!");
        messageyml.put("DoesntContainSubCommands", "&a&lThis command doesn't contain any sub commands!");
        messageyml.put("MessagereloadedSucessful", "&c&lThe Messages has now been reloaded.");
        messageyml.put("MobListConfigreloadedSucessful", "&a&lThe Mob list config cache has now been reloaded.");
        return messageyml;
    }

}
