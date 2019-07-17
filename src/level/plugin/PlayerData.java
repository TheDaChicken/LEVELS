package level.plugin;

import com.nametagedit.plugin.NametagEdit;
import level.plugin.CustomEvents.LevelUpEvent;
import level.plugin.Errors.CantChangeThatManyPoints;
import level.plugin.Errors.MaxLevel;
import level.plugin.Leaderboard.LeaderboardHandler;
import level.plugin.Leaderboard.PositionInfo;
import level.plugin.SupportedPluginsClasses.Vault;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    //This has changed class to handle new storage places and for easy access to peoples info like their level for online players!


    public Player player;
    public String username;
    private UUID uuid;
    public int MaxPrefixNumber;

    //OLD METHODS (CHANGED TO FIT NEW CODE):
    private int runnable = 0; //FOR LEVELUPACTIONBAR
    private int LevelPrefixRunnable = 0; //FOR LEVEL ON TOP OF HEAD

    //For PlayerPointsTime Handler.
    public long PLAYER_JOIN_MILLIS = 0;
    public int LAST_SECONDS_DIFFERENCE = 0;

    public PlayerData(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        //Loads the player data!
        //Why didn't I think of this before... I used a method for this.
        
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(new File(JavaPlugin.getPlugin(Main.class).getDataFolder(), "levelsconfig.yml"));

        //IF PLAYER IS NOT IN STORAGE SETS DEFAULT 0 LEVEL AND POINTS IN STORAGE
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
            if (yml.getString("Level." + uuid.toString()) == null) {
                yml.set("Level." + uuid, 0);
                yml.set("Points." + uuid, 0);
                try {
                    yml.save(config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (yml.getStringList("Users") == null) {
                List<String> users = new ArrayList<>();
                users.add(uuid.toString());
                yml.set("Users", users);
                try {
                    yml.save(config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!yml.getStringList("Users").contains(uuid.toString())) {
                List<String> users = yml.getStringList("Users");
                users.add(uuid.toString());
                yml.set("Users", users);
                try {
                    yml.save(config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (Main.scoreboard != null) {
            LevelPrefixRunnable = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
                if (Main.isPlayerOnline(username)) {
                    if (Levelyml.getBoolean("SupportforNameTagEdit")) {
                        String levelstringprefix = "" + getLevelString() + " ";
                        String levelstringsuffix = " " + getLevelString() + "";
                        if (Levelyml.getBoolean("EnableLevelOnPrefix")) {
                            NametagEdit.getApi().setPrefix(player, levelstringprefix);
                        }
                        if (Levelyml.getBoolean("EnableLevelOnSuffix")) {
                            NametagEdit.getApi().setSuffix(player, levelstringsuffix);
                        }
                    } else {
                        Team team;
                        String level = String.valueOf(getLevel());
                        String levelstring = getLevelString();
                        if (Main.scoreboard.getTeam(level) == null) {
                            team = Main.scoreboard.registerNewTeam(level);
                        } else {
                            team = Main.scoreboard.getTeam(level);
                        }
                        if (Main.scoreboard.getEntryTeam(player.getName()) != null) {
                            if (!Main.scoreboard.getEntryTeam(player.getName()).getName().equalsIgnoreCase(team.getName())) {
                                Main.scoreboard.getEntryTeam(player.getName()).removeEntry(player.getName());
                            } else {
                                return;
                            }
                        }
                        team.addEntry(player.getName());
                        if (Levelyml.getBoolean("EnableLevelOnPrefix")) {
                            if (!team.getPrefix().equalsIgnoreCase(levelstring + " ")) {
                                team.setPrefix(levelstring + " ");
                            }
                        } else if (Levelyml.getBoolean("EnableLevelOnSuffix")) {
                            if (team.getSuffix().equalsIgnoreCase(levelstring)) {
                                team.setSuffix(levelstring);
                            }
                        }
                        player.setScoreboard(Main.scoreboard);
                    }
                } else {
                    Bukkit.getScheduler().cancelTask(LevelPrefixRunnable);
                }
            }, 0, 20L);
        }

        // This is to handle Player Time To Points.
        PLAYER_JOIN_MILLIS = System.currentTimeMillis();
    }

    public void Addpoints(int result) throws MaxLevel {
        int templevel = getLevel();
        int temppoints = getPoints();
        if (isMaxLevelOn()) {
            if (templevel > getMaxLevel() || temppoints == getMaxLevel()) {
                throw (new MaxLevel());
            }
        }
        int maxpoints = getMaxPoints(); //ADDED TO GET NEW MAX POINTS METHOD INTO THE OLD.

        temppoints = Math.abs(temppoints + result);

        if (temppoints == maxpoints) { //USED TO JUST ADD ONE LEVEL IF MAX POINTS IS THE SAME AS THE POINTS
            int together = Math.abs(maxpoints - temppoints);
            if (!setPoints(together)) {
                player.sendMessage(Messages.StoragePlaceNotWorking);
                return;
            }
            if (!setLevel(templevel + 1)) {
                player.sendMessage(Messages.StoragePlaceNotWorking);
                return;
            }
            LevelUpActionBar();
        } else {
            if (temppoints > maxpoints) { //MORE THEN THE LEVEL MAX POINTS
                synchronized (this) {
                    int together = Math.abs(maxpoints - temppoints);
                    LevelUpActionBar();
                    templevel++;
                    while (MoreLevelingUP(together)) {
                        int a = Math.abs(maxpoints - together);
                        templevel++;
                        together = a;
                    }
                    if (!setPoints(together)) {
                        player.sendMessage(Messages.StoragePlaceNotWorking);
                        return;
                    }
                    if (!setLevel(templevel)) {
                        player.sendMessage(Messages.StoragePlaceNotWorking);
                    }
                }
            } else {
                //LESS THEN THE LEVEL MAX POINTS
                if (!setPoints(temppoints)) {
                    player.sendMessage(Messages.StoragePlaceNotWorking);
                    return;
                }
                if (result > 0) {
                    runPointsMessage(result);
                }
            }
        }
    }


    public void SubtractPoints(int result) {
        int templevel = getLevel();
        int temppoints = getPoints();

        int maxpoints = getMaxPoints();

        temppoints = Math.abs(temppoints - result);
        if (temppoints == 0) {
            if (!setPoints(maxpoints - temppoints - 1)) {
                player.sendMessage(Messages.StoragePlaceNotWorking);
                return;
            }
            if (!setLevel(templevel - 1)) {
                player.sendMessage(Messages.StoragePlaceNotWorking);
                return;
            }
            LevelUpActionBar();
        } else {
            if (temppoints > maxpoints) { //MORE THEN THE LEVEL MAX POINTS
                synchronized (this) {
                    int together = Math.abs(temppoints - maxpoints);
                    LevelUpActionBar();
                    templevel--;
                    while (MoreLevelingUP(together)) {
                        together = Math.abs(maxpoints - together);
                        templevel--;
                    }
                    if (!setPoints(together)) {
                        player.sendMessage(Messages.StoragePlaceNotWorking);
                        return;
                    }
                    if (!setLevel(templevel)) {
                        player.sendMessage(Messages.StoragePlaceNotWorking);
                    }
                }
            } else {
                if (!setPoints(temppoints)) {
                    player.sendMessage(Messages.StoragePlaceNotWorking);
                    return;
                }
                if (result != 0) {
                    runSubtractPointsMessage(result);
                }
            }
        }
    }

    private void runPointsMessage(int points) {
        File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(Config);
        String message = Messages.AddPointsMessage(player, points);

        if (Levelyml.getString("AddPointsMessageLocation") != null) {
            if (Levelyml.getString("AddPointsMessageLocation").equalsIgnoreCase("ACTIONBAR")) {
                if (Main.lib != null) {
                    Main.lib.sendActionBar(player, message);
                } else {
                    player.sendMessage(message);
                }
            }
            if (Levelyml.getString("AddPointsMessageLocation").equalsIgnoreCase("CHAT")) {
                player.sendMessage(message);
            }
        } else {
            player.sendMessage(message);
        }


    }

    //TODO WORK ON SUBTRACTING
    private void runSubtractPointsMessage(int points) {
        File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(Config);
        String message = Messages.SubtractPointsMessage(points);

        if (Levelyml.getString("SubtractPointsMessageLocation") != null) {
            if (Levelyml.getString("SubtractPointsMessageLocation").equalsIgnoreCase("ACTIONBAR")) {
                if (Main.lib != null) {
                    Main.lib.sendActionBar(player, message);
                } else {
                    player.sendMessage(message);
                }
            }
            if (Levelyml.getString("SubtractPointsMessageLocation").equalsIgnoreCase("CHAT")) {
                player.sendMessage(message);
            }
        } else {
            //Since people might not delete their LevelConfig file when updating, I chose to check the AddPointsMessageLocation
            if (Levelyml.getString("AddPointsMessageLocation") != null) {
                if (Levelyml.getString("AddPointsMessageLocation").equalsIgnoreCase("ACTIONBAR")) {
                    if (Main.lib != null) {
                        Main.lib.sendActionBar(player, message);
                    } else {
                        player.sendMessage(message);
                    }
                }
                if (Levelyml.getString("AddPointsMessageLocation").equalsIgnoreCase("CHAT")) {
                    player.sendMessage(message);
                }
            } else {
                player.sendMessage(message);
            }
        }
    }

    //NEW METHODS (MADE TO GET THE STUFF FROM THE CONFIG THAT IS STORED IN THIS CLASS AND TO STILL RUN THE OLD CODE FROM THE OLD VERSION)

    private boolean MoreLevelingUP(int points) {
        int maxpoints = getMaxPoints(); //ADDED TO GET NEW MAX POINTS METHOD INTO THE OLD.
        return points > maxpoints || points == maxpoints;
    }

    public void AddLevel(int b) throws MaxLevel {
        int level = getLevel();
        if (isMaxLevelOn()) {
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

        if (!setLevel(a)) {
            player.sendMessage(Messages.StoragePlaceNotWorking);
            return;
        }

        LevelUpActionBar();
    }

    public void ChangeLevel(int a) throws MaxLevel {
        if (isMaxLevelOn()) {
            if (a > getMaxLevel()) {
                throw (new MaxLevel());
            }
        }

        if (!setLevel(a)) {
            player.sendMessage(Messages.StoragePlaceNotWorking);
            return;
        }

        LevelUpActionBar();
    }

    public void ChangePoints(int points) throws MaxLevel, CantChangeThatManyPoints {
        int level = getLevel();
        int maxpoints = getMaxPoints(); //ADDED TO GET NEW MAX POINTS METHOD INTO THE OLD.

        if (isMaxLevelOn()) {
            if (level > getMaxLevel() || level == getMaxLevel()) {
                throw (new MaxLevel());
            }
        }
        int a = points;

        if (a > maxpoints || a == maxpoints) {
            throw (new CantChangeThatManyPoints());
        } else {
            points = a;
            setPoints(points);
        }
    }

    //NEW KINDA METHODS
    private void LevelUpActionBar() {
        File LevelsConfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration ymlconfig = YamlConfiguration.loadConfiguration(LevelsConfig);
        if (ymlconfig.getBoolean("levelupruncommand")) {
            if (ymlconfig.getBoolean("levelupruncommandcertainperlevel")) {
                if (ymlconfig.getStringList("levelcommands." + getLevel()) == null) {
                    return;
                }
                List<String> commands = ymlconfig.getStringList("levelcommands." + getLevel());
                for (String command : commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }
            } else {
                List<String> commands = ymlconfig.getStringList("levelupruncommandslist");
                for (String command : commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }
            }
        }
        if (Main.lib != null) {
            runnable = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), new Runnable() {
                int test = 10;

                public void run() {
                    if (test != 0) {
                        Main.lib.sendActionBar(player, Messages.LevelUpActionbar(player, getLevel()));
                        test--;
                    } else {
                        Bukkit.getServer().getScheduler().cancelTask(runnable);
                    }
                }
            }, 0L, 10L);
        } else {
            player.sendMessage(Messages.LevelUpActionbar(player, getLevel()));
        }
        LevelUpEvent event = new LevelUpEvent(player, getLevel());
        Bukkit.getPluginManager().callEvent(event);
    }

    public String getLevelString() {
        return getLevelPrefix() + getLevel();
    }

    public String getLevelPrefix() {
        int level = getLevel();
        File LevelConfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(LevelConfig);

        MaxPrefixNumber = Levelyml.getInt("MaxLevelPrefix");
        String levelprefix;

        if (level > MaxPrefixNumber) {
            levelprefix = ChatColor.translateAlternateColorCodes('&', Levelyml.getString("LevelColorPrefix." + MaxPrefixNumber));
        } else {
            levelprefix = ChatColor.translateAlternateColorCodes('&', Levelyml.getString("LevelColorPrefix." + level));
        }
        return levelprefix;
    }

    public int getMaxPoints() {
        File LevelConfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(LevelConfig);
        int MaxLevelPoints = Levelyml.getInt("MaxLevelPoints");
        int level = getLevel();
        if (isMaxLevelOn()) {
            if (getMaxLevel() == level) {
                return 0;
            }
        } else {
            if (MaxLevelPoints < level) {
                return Levelyml.getInt("" + MaxLevelPoints);
            }
        }
        return Levelyml.getInt("" + getLevel());
    }

    public boolean isMaxLevelOn() {
        File LevelConfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(LevelConfig);
        String Maxlevel = Levelyml.getString("MaxLevel");
        return !Maxlevel.equalsIgnoreCase("no") && !Maxlevel.equalsIgnoreCase("ignore");
    }

    public int getMaxLevel() {
        File LevelConfig = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration Levelyml = YamlConfiguration.loadConfiguration(LevelConfig);
        String Maxlevel = Levelyml.getString("MaxLevel");
        if (!Maxlevel.equalsIgnoreCase("no")) {
            return Integer.parseInt(Maxlevel);
        } else {
            return 0;
        }
    }

    public boolean setLevel(int level) {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
            // Sets Level
            yml.set("Level." + uuid, level);
            YamlConfiguration levels_config = YamlConfiguration.loadConfiguration(new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml"));
            if (levels_config.contains("setLevels-To-Minecraft-Levels")) {
                if (levels_config.getBoolean("setLevels-To-Minecraft-Levels")) {
                    player.setLevel(Main.playerData.get(player).getLevel());
                }
            }
            try {
                yml.save(config);
                AddLevelPermission(level);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
            try {
                synchronized (this) {
                    Main.openConnectionMYSQL();
                    Statement statement = Main.statement;
                    String query = "SELECT * FROM PlayerData WHERE UUID='" + uuid + "'";
                    ResultSet rs = statement.executeQuery(query);
                    boolean exists = false;

                    if (rs.next()) {
                        exists = rs.getString("Level") != null;
                    }
                    String test;
                    if (exists) {
                        test = "UPDATE PlayerData set Level=\"" + level + "\" where UUID=\"" + uuid + "\"";
                    } else {
                        test = "INSERT INTO PlayerData (UUID, Level) VALUES ('" + uuid + "', " + level + ")" + ";";
                    }
                    statement.executeUpdate(test);
                    AddLevelPermission(level);
                    return true;
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean setPoints(int points) {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
            // Sets Level
            yml.set("Points." + uuid, points);
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
                synchronized (this) {
                    Main.openConnectionMYSQL();
                    Statement statement = Main.statement;
                    String query = "SELECT * FROM PlayerData WHERE UUID='" + uuid + "'";
                    ResultSet rs = statement.executeQuery(query);
                    boolean exists = false;

                    if (rs.next()) {
                        exists = rs.getString("Level") != null;
                    }
                    String test;
                    if (exists) {
                        test = "UPDATE PlayerData set Points=\"" + points + "\" where UUID=\"" + uuid + "\"";
                    } else {
                        test = "INSERT INTO PlayerData (UUID, Points) VALUES ('" + uuid + "', " + points + ")" + ";";
                    }
                    statement.executeUpdate(test);
                    return true;
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public int getLevel() {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
            return yml.getInt("Level." + uuid);
        }
        if (StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
            try {
                synchronized (this) {
                    Main.openConnectionMYSQL();
                    Statement statement = Main.statement;
                    // Create connection and statement
                    String query = "SELECT * FROM PlayerData WHERE UUID='" + uuid + "'";
                    ResultSet rs;
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
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                return 0;
            }
        }

        return 0;
    }


    public int getPoints() {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
            return yml.getInt("Points." + uuid);
        }
        if (StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
            try {
                synchronized (this) {
                    Main.openConnectionMYSQL();
                    Statement statement = Main.statement;
                    // Create connection and statement
                    String query = "SELECT * FROM PlayerData WHERE UUID='" + uuid + "'";
                    ResultSet rs;
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
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    //GETS THE PLAYER POSITION ON THE LEADERBOARD (IF THE LEADERBOARD IS ON)
    public int getLeaderBoardPosition() {
        ArrayList<PositionInfo> scores = LeaderboardHandler.GetLeaderboardPositionInfo();
        PositionInfo score = scores.stream().filter(score2 -> player.getName().equals(score2.username)).findAny().orElse(null);
        if (score == null) {
            return 0;
        } else {
            return score.position;
        }
    }

    private void AddLevelPermission(int level) {
        File config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(config);
        if (Vault.isVaultInstalled()) {//LevelUpPermission
            if (yml.getBoolean("LevelUpPermission")) {
                Vault.GivePermissionThatYouLeveledUpToLevel(player, level);
            }
        }
    }

}