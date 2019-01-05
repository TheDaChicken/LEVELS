package level.plugin.API;

import level.plugin.OfflinePlayerMethods;
import level.plugin.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.Errors.MaxLevel;

public class LevelsPlugin {


    public static int getPlayersLevel(Player player) {
        if (Main.playerData.get(player) == null) {
            Main.playerData.put(player, new PlayerData(player));
        }
        return Main.playerData.get(player).getLevel();
    }

    public static int getPlayersLevel(OfflinePlayer player) {
        return OfflinePlayerMethods.getLevel(player);
    }

    public static int getPlayersPoints(Player player) {
        return Main.playerData.get(player).getPoints();
    }

    public static int getPlayersPoints(OfflinePlayer player) {
        return OfflinePlayerMethods.getPoints(player);
    }

    public static int getPlayersMaxPoints(Player player) {
        return Main.playerData.get(player).getMaxPoints();
    }

    public static int getPlayersMaxPoints(OfflinePlayer player) {
        return OfflinePlayerMethods.getMaxPoints(player);
    }

    public static String getPlayersLevelString(Player player) {
        return Main.playerData.get(player).getLevelString();
    }

    public static String getPlayersLevelString(OfflinePlayer player) {
        return OfflinePlayerMethods.getLevelString(player);
    }

    public static void AddPointsToPlayer(Player player, int points) {
        try {
            Main.playerData.get(player).Addpoints(points);
        } catch (MaxLevel e) {
            //e.printStackTrace();
            player.sendMessage(Messages.AddPointsMaxLevelCatchMessage);
        }

    }

    public static void ChangeLevelToPLayer(Player player, int level) {
        try {
            Main.playerData.get(player).ChangeLevel(level);
        } catch (MaxLevel e) {
            //e.printStackTrace();
            player.sendMessage(Messages.AddLevelMaxLevelCatchMessage);
        }
    }

    public static void AddLevelToPlayer(Player player, int level) {
        try {
            Main.playerData.get(player).AddLevel(level);
        } catch (MaxLevel e) {
            //e.printStackTrace();
            player.sendMessage(Messages.AddLevelMaxLevelCatchMessage);
        }
    }

}
