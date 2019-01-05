package level.plugin.Leaderboard;

import level.plugin.*;
import level.plugin.API.LevelsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LeaderboardHandler {

    public static boolean getEnabled() {
        File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(Config);
        if(cfg.getBoolean("Leaderboard.Enable")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getCommand() {
        File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(Config);

        return cfg.getString("Leaderboard.LevelboardCommand");
    }

    public static int getLowestLeaderboardPosition() {
        File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(Config);

        return cfg.getInt("Leaderboard.LowestLeaderboardPosition");
    }


    public static int getPlayerLevel(String uuid) {
        UUID uniqueId = UUID.fromString(uuid);
        int level;

        if(Bukkit.getPlayer(uniqueId) == null) {
            level = LevelsPlugin.getPlayersLevel(Bukkit.getOfflinePlayer(uniqueId));
        } else {
            level = Main.playerData.get(Bukkit.getPlayer(uniqueId)).getLevel();
        }

        return level;
    }

    public static String getPlayerName(String uuid) {
        UUID uniqueId = UUID.fromString(uuid);
        String name;

        if(Bukkit.getPlayer(uniqueId) == null) {
            name = Bukkit.getOfflinePlayer(uniqueId).getName();
        } else {
            name = Bukkit.getPlayer(uniqueId).getName();
        }

        return name;
    }

    public static String GetLeaderboardString() {
        ArrayList<LeaderboardScore> scores = getUUIDList();
        ArrayList<LeaderboardScore> sortedscore = sort(scores);

        String highscoreString = "";

        int i = 0;
        int x;
        if(scores.size() < getLowestLeaderboardPosition()) {
            x = scores.size();
        } else {
            x = getLowestLeaderboardPosition();
        }

        while (i < x) {
            highscoreString += (i + 1) + ". " + scores.get(i).name + ": " + scores.get(i).level + "\n";
            i++;
        }
        return highscoreString;
    }

    public static ArrayList<String> GetLeaderboardArray() {
        ArrayList<LeaderboardScore> scores = getUUIDList();

        ArrayList<LeaderboardScore> sortedscore = sort(scores);

        ArrayList<String> highscoreString = new ArrayList<String>();

        int i = 0;
        int x;

        if(scores.size() < getLowestLeaderboardPosition()) {
            x = scores.size();
        } else {
            x = getLowestLeaderboardPosition();
        }

        while (i < x) {
            //highscoreString.add(Integer.valueOf(i + 1) + ". " + scores.get(i).name + ": " + scores.get(i).level + "");
            highscoreString.add(Messages.LeaderboardPositionMessage(Integer.valueOf(i + 1), scores.get(i).name, scores.get(i).level));
            i++;
        }

        return highscoreString;
    }

    public static ArrayList<PositionInfo> GetLeaderboardPositionInfo() {
        ArrayList<LeaderboardScore> scores = getUUIDList();
        ArrayList<LeaderboardScore> sortedscore = sort(scores);
        ArrayList<PositionInfo> highscoreString = new ArrayList<PositionInfo>();

        int i = 0;
        int x = scores.size();

        while (i < x) {
            //highscoreString.add(Integer.valueOf(i + 1) + ". " + scores.get(i).name + ": " + scores.get(i).level + "");
            highscoreString.add(new PositionInfo(scores.get(i).name, Integer.valueOf(i + 1), scores.get(i).level));
            //highscoreString.add(Messages.LeaderboardPositionMessage(Integer.valueOf(i + 1), scores.get(i).name, scores.get(i).level));
            i++;
        }
        return highscoreString;
    }


    public static ArrayList<LeaderboardScore> sort(ArrayList<LeaderboardScore> score) {
        LeaderboardComparator comparator = new LeaderboardComparator();

        Collections.sort(score, comparator);

        return score;
    }

    public static ArrayList<LeaderboardScore> getUUIDList() {
        ArrayList<LeaderboardScore> arrayList = new ArrayList<LeaderboardScore>();

        if(StorageOptions.isStorageOption(StorageOptions.FILE)) {
            File data = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(data);
            for(String uuid : cfg.getStringList("Users")) {
                String name = getPlayerName(uuid);

                if(Bukkit.getPlayer(name) != null) {
                    if(Main.playerData.get(Bukkit.getPlayer(name)) == null) {
                        Main.playerData.put(Bukkit.getPlayer(name), new PlayerData(Bukkit.getPlayer(name)));
                    }
                    arrayList.add(new LeaderboardScore(name, Main.playerData.get(Bukkit.getPlayer(name)).getLevel() ));
                } else {
                    int level = OfflinePlayerMethods.getLevel(name);
                    arrayList.add(new LeaderboardScore(name, level));
                }
            }
        }

        if(StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
            try {
                synchronized (LeaderboardHandler.class) {
                    Main.openConnectionMYSQL();
                    Statement statement = Main.statement;
                    String query = "SELECT * FROM PlayerData";
                    ResultSet rs = statement.executeQuery(query);

                    while(rs.next()) {
                        int level = rs.getInt("Level");
                        String uuid = rs.getString("uuid");
                        String name = getPlayerName(uuid);
                        arrayList.add(new LeaderboardScore(name, level));
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }


}
