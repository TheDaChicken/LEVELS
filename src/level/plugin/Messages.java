package level.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Messages {

    private static CustomJavaPlugin plugin = CustomJavaPlugin.getPlugin(Main.class);
    private static FileConfiguration messageFile = plugin.getMessageFile();

    public static void reloadMessages() {
        plugin.reloadConfig();
    }


    public static String getMessage(Player player, String message_name) {
        String message = plugin.getMessageFile().getString(message_name);
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            return PlaceHolderAPIString(player, message);
        }
        return null;
    }

    private static String PlaceHolderAPIString(Player player, String string) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
        } else {
            return string;
        }
    }

}
