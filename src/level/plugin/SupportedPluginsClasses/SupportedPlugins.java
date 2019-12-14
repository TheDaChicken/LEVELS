package level.plugin.SupportedPluginsClasses;

import level.plugin.CustomJavaPlugin;
import level.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class SupportedPlugins {

    public static void setupSupportedPlugins() {
        if (isPlaceHolderInstalled()) {
            // Check if PlaceholderAPI is installed.
            Bukkit.getConsoleSender().sendMessage("PlaceholderAPI found!");
            new PlaceHolderAPI(CustomJavaPlugin.getPlugin(Main.class)).register();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabling the PlaceHolders! - Using PlaceHolder PlaceholderExpansion.");
        }
        if (isNameTagEditInstalled()) {
            Bukkit.getConsoleSender().sendMessage("NameTagEdit found!");
        }
    }

    public static boolean isPlaceHolderInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static boolean isNameTagEditInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("NametagEdit");
    }

}
