package level.plugin.SupportedPluginsClasses;

import level.plugin.Main;
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
        //TODO ALL THE PLACEHOLDERS
        return null;
    }
}