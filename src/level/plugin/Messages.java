package level.plugin;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    public static String getMessage(Player player, String message_name, HashMap<String, String> extra_hashmap) {
        Object object_message = plugin.getMessageFile().get(message_name);
        String message = null;
        if (object_message instanceof String) {
            message = (String) object_message;
        } else if (object_message instanceof List) {
            message = StringUtils.join((List) object_message, "\n");
        }
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            return PlaceHolderString(player, message, extra_hashmap);
        }
        return null;
    }

    public static String getMessage(PlayerData playerData, String message_name) {
        Object object_message = plugin.getMessageFile().get(message_name);
        String message = null;
        if (object_message instanceof String) {
            message = (String) object_message;
        } else if (object_message instanceof List) {
            message = StringUtils.join((List) object_message, "\n");
        }
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            return PlaceHolderString(playerData, message);
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
            playerAddToHashMap(player, hashMap);
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

    private static String PlaceHolderString(Player player, String string, HashMap<String, String> extra_hashmap) {
        HashMap<String, String> hashMap = new HashMap<String, String>(extra_hashmap);
        if (player != null) {
            playerAddToHashMap(player, hashMap);
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

    private static String PlaceHolderString(PlayerData playerData, String string) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        playerAddToHashMap(playerData, hashMap);

        if (hashMap.size() != 0) {
            string = replaceParams(hashMap, string);
        }
        if (playerData.getPlayerObject() != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(playerData.getPlayerObject(), string);
        }

        return string;
    }

    private static void playerAddToHashMap(Player player, HashMap<String, String> hashMap) {
        hashMap.put("playername", player.getName());
        if (!Main.onlinePlayers.containsKey(player)) {
            Main.onlinePlayers.put(player, new PlayerData(player));
        }

        hashMap.put("levelnumber", String.valueOf(Main.onlinePlayers.get(player).getLevel()));
        hashMap.put("points", String.valueOf(Main.onlinePlayers.get(player).getPoints()));
        hashMap.put("levelprefix", String.valueOf(Main.onlinePlayers.get(player).getLevelPrefix()));
        hashMap.put("maxpoints", String.valueOf(Main.onlinePlayers.get(player).getStoredMaxPoints()));
        hashMap.put("random_color", "" + ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16))));
    }


    private static void playerAddToHashMap(PlayerData playerData, HashMap<String, String> hashMap) {
        hashMap.put("playername", playerData.getPlayerName());
        hashMap.put("levelnumber", String.valueOf(playerData.getLevel()));
        hashMap.put("points", String.valueOf(playerData.getPoints()));
        hashMap.put("levelprefix", String.valueOf(playerData.getLevelPrefix()));
        hashMap.put("maxpoints", String.valueOf(playerData.getStoredMaxPoints()));
        hashMap.put("random_color", "" + ChatColor.getByChar(Integer.toHexString(new Random().nextInt(16))));
    }

}
