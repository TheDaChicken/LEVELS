package level.plugin.SupportedPluginsClasses;

import level.plugin.Main;
import level.plugin.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceHolderAPI extends PlaceholderExpansion {

    private Main plugin;

    public PlaceHolderAPI(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "Levels";
    }

    @Override
    public String getAuthor() {
        return "TheDaChicken";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        PlayerData playerData = Main.getPlayerData(player);
        if (identifier.equalsIgnoreCase("level_string")) {
            return String.valueOf(playerData.getLevelString());
        }
        if (identifier.equalsIgnoreCase("level_number")) {
            return String.valueOf(playerData.getLevel());
        }
        if (identifier.equalsIgnoreCase("points")) {
            return String.valueOf(playerData.getPoints());
        }
        if (identifier.equalsIgnoreCase("points_max")) {
            return String.valueOf(playerData.getStoredMaxPoints());
        }
        if(identifier.equalsIgnoreCase("points_progress")) {
            return String.valueOf(playerData.getLevelProgress());
        }
        return null;
    }
}