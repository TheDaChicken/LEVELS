package level.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import level.plugin.API.LevelsPlugin;
import level.plugin.Errors.TheUserhasNotplayedBefore;
import level.plugin.Leaderboard.LeaderboardHandler;
import level.plugin.Leaderboard.LeaderboardScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Messages {

    public static YamlConfiguration Config = YamlConfiguration.loadConfiguration(new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "messages.yml"));

	public static String AddPointsMaxLevelCatchMessage = ChatColor.translateAlternateColorCodes('&', Config.getString("AddPointsMaxLevelCatchMessage"));
	
	public static String PlayerhasNotJoinedServerBefore = ChatColor.translateAlternateColorCodes('&', Config.getString("PlayerhasNotJoinedServerBefore"));
	
	public static String ProblemAddingPoints = ChatColor.translateAlternateColorCodes('&', Config.getString("ProblemAddingPoints"));

	public static String ProblemAddingLevel = ChatColor.translateAlternateColorCodes('&', Config.getString("ProblemAddingLevel"));
	
	public static String AddLevelMaxLevelCatchMessage = ChatColor.translateAlternateColorCodes('&', Config.getString("AddLevelMaxLevelCatchMessage"));

	public static String AddLevelUsage = ChatColor.translateAlternateColorCodes('&', Config.getString("AddLevelUsage"));
	
	public static String AddPointsUsage = ChatColor.translateAlternateColorCodes('&', Config.getString("AddPointsUsage"));
	
	public static String YouNeedOP = ChatColor.translateAlternateColorCodes('&', Config.getString("YouNeedOP"));

	public static String LevelUpActionbar(int number) {

        String message = ChatColor.translateAlternateColorCodes('&', Config.getString("LevelUpActionbar")).replace("%number%", String.valueOf(number));

		String reformat = String.valueOf(ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16)))) + message;
		
		return reformat;
	}
	
	public static String LevelHasSet = ChatColor.translateAlternateColorCodes('&', Config.getString("LevelHasSet"));
	
	public static ArrayList<String> StatsInfoOnlySelf(Player player) {
        ArrayList<String> test = new ArrayList<String>();
        int Level = Main.playerData.get(player).getLevel();
        int Point = Main.playerData.get(player).getPoints();
        int maxPoint = Main.playerData.get(player).getMaxPoints();
        String levelprefix = Main.playerData.get(player).getLevelPrefix();
        int maxLevel = Main.playerData.get(player).getMaxLevel();
        List<String> temp;
        if(Main.playerData.get(player).isMaxlevelOn()) {
            if (maxLevel != Level) {
                temp = Config.getStringList("StatsInfoOnlySelf");
            } else {
                temp = Config.getStringList("StatsInfoOnlySelfMaxLevel");
            }
        } else {
            temp = Config.getStringList("StatsInfoOnlySelf");
        }

        ArrayList<String> Finished = new ArrayList<String>();
        for (String convert : temp) {
            convert = ChatColor.translateAlternateColorCodes('&', convert).replace("%levelprefix%", levelprefix).replace("%levelnumber%", String.valueOf(Level)).replace("%points%", String.valueOf(Point)).replace("%maxpoints%", String.valueOf(maxPoint));
            Finished.add(convert);
        }

        return Finished;
        //String StatsInfoOnlySelf = ChatColor.translateAlternateColorCodes('&', Config.getString("StatsInfoOnlySelf")).replace("%levelprefix%", levelprefix).replace("%levelnumber%", String.valueOf(Level)).replace("%points%", String.valueOf(Point)).replace("%maxpoints%", String.valueOf(maxPoint));
        //String StatsInfoOnlySelfMaxLevel  = ChatColor.translateAlternateColorCodes('&', Config.getString("StatsInfoOnlySelfMaxLevel")).replace("%levelprefix%", levelprefix).replace("%levelnumber%", String.valueOf(Level)).replace("%points%", String.valueOf(Point)).replace("%maxpoints%", String.valueOf(maxPoint));
    }

	public static String ChangePointsUsage = ChatColor.translateAlternateColorCodes('&', Config.getString("ChangePointsUsage"));

	public static String CantAddThatManyPoints = ChatColor.translateAlternateColorCodes('&', Config.getString("CantAddThatManyPoints"));
	
	public static String ChangePointsMaxLevelCatchMessage = ChatColor.translateAlternateColorCodes('&', Config.getString("ChangePointsMaxLevelCatchMessage"));

	public static String ChangeLevelUsage = ChatColor.translateAlternateColorCodes('&', Config.getString("ChangeLevelUsage"));

	public static String ProblemChangingLevel = ChatColor.translateAlternateColorCodes('&', Config.getString("ProblemChangingLevel"));

	public static String LevelHigherThenMaxLevel = ChatColor.translateAlternateColorCodes('&', Config.getString("LevelHigherThenMaxLevel"));
	
	
	public static ArrayList<String> StatsInfoPlayers(Player player) {
        ArrayList<String> test = new ArrayList<String>();
        int Level = Main.playerData.get(player).getLevel();
        int Point = Main.playerData.get(player).getPoints();
        int maxPoint = Main.playerData.get(player).getMaxPoints();
        String levelprefix = Main.playerData.get(player).getLevelPrefix();
        int maxLevel = Main.playerData.get(player).getMaxLevel();

        List<String> temp;

        if(Main.playerData.get(player).isMaxlevelOn()) {
            if (maxLevel != Level) {
                temp = Config.getStringList("StatsInfoPlayers");
            } else {
                temp = Config.getStringList("StatsInfoPlayersMaxLevel");
            }
        } else {
            temp = Config.getStringList("StatsInfoPlayers");
        }

        ArrayList<String> Finished = new ArrayList<String>();
        for (String convert : temp) {
            convert = ChatColor.translateAlternateColorCodes('&', convert).replace("%levelprefix%", levelprefix).replace("%levelnumber%", String.valueOf(Level)).replace("%points%", String.valueOf(Point)).replace("%maxpoints%", String.valueOf(maxPoint)).replace("%player_name%", player.getName());
            Finished.add(convert);
        }
        return Finished;
	}

    // OfflinePlayers
	public static ArrayList<String> StatsInfoPlayers(OfflinePlayer player) {

		ArrayList<String> test = new ArrayList<String>();

		String Level = OfflinePlayerMethods.getLevelString(player);

		int Point = OfflinePlayerMethods.getPoints(player);

		int maxPoint = OfflinePlayerMethods.getMaxPoints(player);

		test.add(ChatColor.AQUA + "Level " + player.getName() + ChatColor.BOLD + " > "
								+ Level);
		test.add(ChatColor.GRAY + "" + ChatColor.BOLD + "Points: " + Point + "/" + maxPoint);

		return test;
	}

	public static String AddPointsMessage(int result) {

	    String message = ChatColor.translateAlternateColorCodes('&', Config.getString("AddPointsMessage")).replace("%amountofpoints%", String.valueOf(result));

	    return message;

    }


    public static String MYSQLNotenabledinConfig = ChatColor.translateAlternateColorCodes('&', Config.getString("MYSQLNotenabledinConfig"));

	public static String ProblemwithMYSQLServer = ChatColor.translateAlternateColorCodes('&', Config.getString("ProblemwithMYSQLServer"));

    public static String LevelHologramCommandUsage() {
        List<String> usage = Config.getStringList("LevelLeaderboardCommandUsage");

        ArrayList<String> finished = new ArrayList<>();

        for(String line : usage) {
            finished.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return String.join("\n", finished);
    }

    public static String DeleteHologram = ChatColor.translateAlternateColorCodes('&', Config.getString("DeleteHologram"));

    public static String HologramAlreadyDeleted = ChatColor.translateAlternateColorCodes('&', Config.getString("HologramAlreadyDeleted"));

    public static String YouNeedtoBePlayer = ChatColor.translateAlternateColorCodes('&', Config.getString("YouNeedtoBePlayer"));

    private static File file = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
    private static YamlConfiguration levelconfig = YamlConfiguration.loadConfiguration(file);

    public static String LeaderboardPositionMessage(int position, String name, int level) {

        String positionprefix = levelconfig.getString("Leaderboard.PositionPrefix." + String.valueOf(position));

        String message = ChatColor.translateAlternateColorCodes('&', levelconfig.getString("Leaderboard.LeaderboardMessage").replace("%positionprefix%", positionprefix).replace("%player_name%", name).replace("%level_number%", String.valueOf(level)));

        return message;
    }

    public static String ChangeLevelsMaxLevelCatchMessage = ChatColor.translateAlternateColorCodes('&', Config.getString("ChangeLevelsMaxLevelCatchMessage"));


    public static String LeaderHeadsLabelSign() {

        List<String> usage = Config.getStringList("LeaderHeadsLabelSign");

        ArrayList<String> finished = new ArrayList<>();

        for(String line : usage) {
            finished.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return String.join("\n", finished);
    }

    public static String leaderHeadNotANumber(String number) {
        return ChatColor.translateAlternateColorCodes('&', Config.getString("leaderHeadNotANumber")).replace("%pos%", number);
    }

    public static String LeaderHeadSignCreated(String number) {
        return ChatColor.translateAlternateColorCodes('&', Config.getString("LeaderHeadSignCreated")).replace("%pos%", number);
    }

    public static ArrayList<String> SignMessage(String username, int intposition) {
        ArrayList<String> finished = new ArrayList<String>();

        String position = String.valueOf(intposition);

        List<String> stringList = levelconfig.getStringList("Leaderboard.LeaderHead." + "SignMessage");

        String name = username;

        int level;

        if(Bukkit.getPlayer(username) != null) {
            level = LevelsPlugin.getPlayersLevel(Bukkit.getPlayer(username));
        } else {
            try {
                level = LevelsPlugin.getPlayersLevel(Main.getOfflinePlayer(username));
            } catch (TheUserhasNotplayedBefore theUserhasNotplayedBefore) {
                return null;
            }
        }

        for(String message : stringList) {
            finished.add(ChatColor.translateAlternateColorCodes('&', message).replace("%position%", position).replace("%player_name%", name).replace("%level_number%", String.valueOf(level)));
        }

        return finished;
    }

    public static ArrayList<String> SignMessageNoOneThere(int intposition) {
        ArrayList<String> finished = new ArrayList<String>();

        String position = String.valueOf(intposition);

        List<String> stringList = levelconfig.getStringList("Leaderboard.LeaderHead." + "SignMessageNoOneThere");

        for(String message : stringList) {
            finished.add(ChatColor.translateAlternateColorCodes('&', message).replace("%position%", position));
        }

        return finished;
    }

    public static String leaderboardSetHeadUsage = ChatColor.translateAlternateColorCodes('&', Config.getString("leaderboardSetHeadUsage"));

    public static String leaderboardSetHeadNotNumber(String number) { return ChatColor.translateAlternateColorCodes('&', Config.getString("leaderboardSetHeadNotNumber")).replace("%number%", number); }

    public static String PlaceHead = ChatColor.translateAlternateColorCodes('&', Config.getString("PlaceHead"));

    public static String StoragePlaceNotWorking = ChatColor.translateAlternateColorCodes('&', Config.getString("StoragePlaceNotWorking"));

}