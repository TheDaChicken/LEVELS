package level.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

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
            return PlaceHolderString(player, message);
        }
        return null;
    }

    private static String replaceParams(Map<String, String> hashMap, String template) {
        // Found here, https://stackoverflow.com/a/27815924/8075127
        return hashMap.entrySet().stream().reduce(template, (s, e) -> s.replace("%" + e.getKey() + "%", e.getValue()),
                (s, s2) -> s);
    }

    private static String PlaceHolderString(Player player, String string) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (player != null) {
            hashMap.put("plugin_player_name", player.getName());
            hashMap.put("plugin_level", String.valueOf(Main.onlinePlayers.get(player).getLevel()));
            hashMap.put("plugin_points", String.valueOf(Main.onlinePlayers.get(player).getPoints()));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
        } else {
            return string;
        }
    }

}
