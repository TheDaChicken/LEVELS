package level.plugin;

import level.plugin.Commands.AddPoints;
import level.plugin.Errors.CantChangeThatManyPoints;
import level.plugin.Errors.MaxLevel;
import level.plugin.Errors.TheUserhasNotplayedBefore;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OfflinePlayerMethods {

    //TODO MAKE THIS CLASS SMALLER
    //TODO MAYBE REMOVE ALL DEPRECATED STUFF (IF CAN), MOSTLY REMOVED IT BY MOVING IT TO A DIFFERENT CLASS :/

    public static int getLevel(String name) {
        OfflinePlayer player = null;
        try {
            player = Main.getOfflinePlayer(name);
        } catch (TheUserhasNotplayedBefore theUserhasNotplayedBefore) {
            return 0;
        }
        return getLevel(player);
    }

    public static int getPoints(String name) {
        OfflinePlayer player = null;
        try {
            player = Main.getOfflinePlayer(name);
        } catch (TheUserhasNotplayedBefore theUserhasNotplayedBefore) {
            return 0;
        }
        return getPoints(name);
    }

    public static int getLevel(OfflinePlayer player) {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
            return yml.getInt("Level." + player.getUniqueId());
        }
        if (StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
            try {
                synchronized (OfflinePlayerMethods.class) {
                    Main.openConnectionMYSQL();
                    Statement statement = Main.statement;
                    // Create connection and statement
                    String query = "SELECT * FROM PlayerData WHERE UUID='" + player.getUniqueId() + "'";
                    ResultSet rs = null;
                    try {
                        rs = statement.executeQuery(query);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    int level = 0;
                    try {
                        if (rs.next()) {
                            level = rs.getInt("Level");
                            if (rs.wasNull()) {
                                return 0;
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    return level;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }


    public static int getPoints(OfflinePlayer player) {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
            return yml.getInt("Points." + player.getUniqueId());
        }
        if (StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
            try {
                synchronized (OfflinePlayerMethods.class) {
                    Main.openConnectionMYSQL();
                    Statement statement = Main.statement;
                    // Create connection and statement
                    String query = "SELECT * FROM PlayerData WHERE UUID='" + player.getUniqueId() + "'";
                    ResultSet rs = null;
                    try {
                        rs = statement.executeQuery(query);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    int level = 0;
                    try {
                        if (rs.next()) {
                            level = rs.getInt("Points");
                            if (rs.wasNull()) {
                                return 0;
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    return level;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }


    public static String getLevelString(OfflinePlayer player) {
        return getLevelPrefix(player) + getLevel(player);
    }

    public static String getLevelPrefix(OfflinePlayer player) {
        int level = getLevel(player);
        File LevelConfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(LevelConfig);

        int maxprefixnumber = Levelyml.getInt("MaxLevelPrefix");
        String levelprefix;

        if (level > maxprefixnumber) {
            levelprefix = ChatColor.translateAlternateColorCodes('&', Levelyml.getString("LevelColorPrefix." + maxprefixnumber));
        } else {
            levelprefix = ChatColor.translateAlternateColorCodes('&', Levelyml.getString("LevelColorPrefix." + level));
        }
        return levelprefix;
    }

    public static int getMaxPoints(OfflinePlayer player) {
        File LevelConfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(LevelConfig);
        int MaxLevelPoints = Levelyml.getInt("MaxLevelPoints");
        int level = getLevel(player);

        if (isMaxlevelOn()) {
            if (getMaxLevel() == level) {
                return 0;
            }
        } else {
            if (MaxLevelPoints < level) {
                return Levelyml.getInt("" + MaxLevelPoints);
            }
        }
        return Levelyml.getInt("" + getLevel(player));
    }

    public static boolean isMaxlevelOn() {
        File LevelConfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(LevelConfig);
        String Maxlevel = Levelyml.getString("MaxLevel");
        return !Maxlevel.equalsIgnoreCase("no") && !Maxlevel.equalsIgnoreCase("ignore");
    }

    public static int getMaxLevel() {
        File LevelConfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(LevelConfig);
        String Maxlevel = Levelyml.getString("MaxLevel");
        if (!Maxlevel.equalsIgnoreCase("no")) {
            return Integer.parseInt(Maxlevel);
        } else {
            return 0;
        }
    }

    public static void ChangeLevel(OfflinePlayer player, int a) throws MaxLevel {
        if (isMaxlevelOn()) {
            if (a > getMaxLevel()) {
                throw (new MaxLevel());
            }
        }
        if (!setLevel(player, a)) {
            return;
        }
        setLevel(player, a);
    }

    public static boolean setLevel(OfflinePlayer player, int level) {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
            // Sets Level
            yml.set("Level." + player.getUniqueId(), level);
            try {
                yml.save(config);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
            try {
                synchronized (OfflinePlayerMethods.class) {
                    Main.openConnectionMYSQL();
                    Statement statement = Main.statement;
                    String query = "SELECT * FROM PlayerData WHERE UUID='" + player.getUniqueId() + "'";
                    ResultSet rs = statement.executeQuery(query);
                    boolean exists = false;

                    if (rs.next()) {
                        exists = rs.getString("Level") != null;
                    }
                    String test;
                    if (exists) {
                        test = "UPDATE PlayerData set Level=\"" + level + "\" where UUID=\"" + player.getUniqueId() + "\"";
                    } else {
                        test = "INSERT INTO PlayerData (UUID, Level) VALUES ('" + player.getUniqueId() + "', " + level + ")" + ";";
                    }
                    statement.executeUpdate(test);
                    return true;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static void ChangePoints(OfflinePlayer player, int points) throws MaxLevel, CantChangeThatManyPoints {
        int level = getLevel(player);
        int maxpoints = getMaxPoints(player); //ADDED TO GET NEW MAX POINTS METHOD INTO THE OLD.

        if (isMaxlevelOn()) {
            if (level > getMaxLevel() || level == getMaxLevel()) {
                throw (new MaxLevel());
            }
        }
        int a = points;

        if (a > maxpoints || a == maxpoints) {
            throw (new CantChangeThatManyPoints());
        } else {
            points = a;
            setPoints(player, points);
        }
    }

    public static boolean setPoints(OfflinePlayer player, int points) {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
            // Sets Level
            yml.set("Points." + player.getUniqueId(), points);
            try {
                yml.save(config);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
            try {
                synchronized (OfflinePlayerMethods.class) {
                    Main.openConnectionMYSQL();
                    Statement statement = Main.statement;
                    String query = "SELECT * FROM PlayerData WHERE UUID='" + player.getUniqueId() + "'";
                    ResultSet rs = statement.executeQuery(query);
                    boolean exists = false;

                    if (rs.next()) {
                        exists = rs.getString("Level") != null;
                    }
                    String test;
                    if (exists) {
                        test = "UPDATE PlayerData set Points=\"" + points + "\" where UUID=\"" + player.getUniqueId() + "\"";
                    } else {
                        test = "INSERT INTO PlayerData (UUID, Points) VALUES ('" + player.getUniqueId() + "', " + points + ")" + ";";
                    }
                    statement.executeUpdate(test);
                    return true;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }


    public static void AddLevel(String username, int b) throws MaxLevel {
        OfflinePlayer offlinePlayer = null;
        try {
            offlinePlayer = Main.getOfflinePlayer(username);
        } catch (TheUserhasNotplayedBefore theUserhasNotplayedBefore) {
            theUserhasNotplayedBefore.printStackTrace();
            return;
        }
        AddLevel(offlinePlayer, b);
    }

    public static void AddLevel(OfflinePlayer offlinePlayer, int b) throws MaxLevel {
        int level = getLevel(offlinePlayer);
        if (isMaxlevelOn()) {
            if (level > getMaxLevel()) {
                throw (new MaxLevel());
            } else {
                int test = Math.abs(level + b);
                if (test > getMaxLevel()) {
                    throw (new MaxLevel());
                }
            }
        }
        int a = Math.abs(level + b);

        if (!setLevel(offlinePlayer, a)) {
            return;
        }
    }

    public static void Addpoints(String username, int result) throws MaxLevel {
        OfflinePlayer offlinePlayer = null;
        try {
            offlinePlayer = Main.getOfflinePlayer(username);
        } catch (TheUserhasNotplayedBefore theUserhasNotplayedBefore) {
            theUserhasNotplayedBefore.printStackTrace();
            return;
        }
        Addpoints(offlinePlayer, result);
    }

    public static void Addpoints(OfflinePlayer offlinePlayer, int result) throws MaxLevel {
        int templevel = getLevel(offlinePlayer);
        int temppoints = getPoints(offlinePlayer);
        if (isMaxlevelOn()) {
            if (templevel > getMaxLevel() || temppoints == getMaxLevel()) {
                throw (new MaxLevel());
            }
        }
        int maxpoints = getMaxPoints(offlinePlayer); //ADDED TO GET NEW MAX POINTS METHOD INTO THE OLD.
        temppoints = Math.abs(temppoints + result);

        if (temppoints == maxpoints) { //USED TO JUST ADD ONE LEVEL IF MAX POINTS IS THE SAME AS THE POINTS
            int together = Math.abs(maxpoints - temppoints);
            if (!setPoints(offlinePlayer, temppoints)) {
                return;
            }
            if (!setLevel(offlinePlayer, templevel + 1)) {
                return;
            }
        } else {
            if (temppoints > maxpoints) { //MORE THEN THE LEVEL MAX POINTS
                synchronized (OfflinePlayerMethods.class) {
                    int together = Math.abs(maxpoints - temppoints);
                    templevel++;
                    while (MoreLevelingUP(offlinePlayer, together)) {
                        int a = Math.abs(maxpoints - together);
                        templevel++;
                        together = a;
                    }
                    if (!setPoints(offlinePlayer, together)) {
                        return;
                    }
                    if (!setLevel(offlinePlayer, templevel)) {
                        return;
                    }
                }
            } else {
                //LESS THEN THE LEVEL MAX POINTS
                setPoints(offlinePlayer, temppoints);
            }
        }

    }

    private static boolean MoreLevelingUP(OfflinePlayer player, int points) {
        int maxpoints = getMaxPoints(player); //ADDED TO GET NEW MAX POINTS METHOD INTO THE OLD.
        return points > maxpoints || points == maxpoints;
    }
}
