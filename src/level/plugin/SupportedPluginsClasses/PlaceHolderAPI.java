package level.plugin.SupportedPluginsClasses;

import level.plugin.API.LevelsPlugin;
import level.plugin.Leaderboard.LeaderboardHandler;
import level.plugin.Leaderboard.PositionInfo;
import level.plugin.Main;
import level.plugin.PlayerData;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlaceHolderAPI extends EZPlaceholderHook {

    private Main ourPlugin;

    public PlaceHolderAPI(Main plugin) {
        super(plugin, "levels");
        this.ourPlugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if(identifier.contains("position_level:")) {
            String position_number = identifier.substring(identifier.lastIndexOf(":") + 1);
            int position = Integer.parseInt(position_number);
            ArrayList<PositionInfo> positionInfoArrayList = LeaderboardHandler.GetLeaderboardPositionInfo();
            PositionInfo score = positionInfoArrayList.stream().filter(score2 -> position == (score2.position)).findAny().orElse(null);
            return String.valueOf(score.level);
        }
        if(identifier.contains("position_username:")) {
            String position_number = identifier.substring(identifier.lastIndexOf(":") + 1);
            int position = Integer.parseInt(position_number);
            ArrayList<PositionInfo> positionInfoArrayList = LeaderboardHandler.GetLeaderboardPositionInfo();
            PositionInfo score = positionInfoArrayList.stream().filter(score2 -> position == (score2.position)).findAny().orElse(null);
            return String.valueOf(score.username);
        }
        if (player == null) {
            return "";
        }
        if (identifier.equalsIgnoreCase("level_string")) {
            return LevelsPlugin.getPlayersLevelString(player);
        }
        if (identifier.equalsIgnoreCase("level_number")) {
            return String.valueOf(LevelsPlugin.getPlayersLevel(player));
        }
        if (identifier.equalsIgnoreCase("points")) {
            return String.valueOf(LevelsPlugin.getPlayersPoints(player));
        }
        if (identifier.equalsIgnoreCase("points_max")) {
            return String.valueOf(LevelsPlugin.getPlayersMaxPoints(player));
        }
        if (identifier.equalsIgnoreCase("position")) {
            if(Main.playerData.get(player) == null) {
                Main.playerData.put(player, new PlayerData(player));
            }
            return String.valueOf(Main.playerData.get(player).getLeaderBoardPosition());
        }
        return null;
    }
}
