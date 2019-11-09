package level.plugin;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages {

    private static CustomJavaPlugin plugin = CustomJavaPlugin.getPlugin(Main.class);
    private static FileConfiguration messageFile = plugin.getMessageFile();

    public static void reloadMessages() {
        plugin.reloadConfig();
    }


    public static String getMessage(Player player, String message_name) {
        Object object_message = plugin.getMessageFile().get(message_name);
        String message = null;
        if (object_message instanceof String) {
            message = (String) object_message;
        } else if (object_message instanceof List) {
            message = StringUtils.join((List) object_message, "\n");
        }
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            return PlaceHolderString(player, message);
        }
        return null;
    }

    private static String replaceParams(Map<String, String> hashMap, String template) {
        // Found here, https://stackoverflow.com/a/39902558/8075127
        return hashMap.entrySet().stream().reduce(template, (s, e) -> s.replace("%" + e.getKey() + "%", e.getValue()),
                (s, s2) -> s);
    }

    private static String PlaceHolderString(Player player, String string) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (player != null) {
            hashMap.put("playername", player.getName());
            if (!Main.onlinePlayers.containsKey(player)) {
                Main.onlinePlayers.put(player, new PlayerData(player));
            }

            hashMap.put("levelnumber", String.valueOf(Main.onlinePlayers.get(player).getLevel()));
            hashMap.put("points", String.valueOf(Main.onlinePlayers.get(player).getPoints()));
        }

        if (hashMap.size() != 0) {
            string = replaceParams(hashMap, string);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
        } else {
            return string;
        }
    }

}
