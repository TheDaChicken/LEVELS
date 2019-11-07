package level.plugin.SupportedPluginsClasses;

import level.plugin.CustomJavaPlugin;
import level.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class SupportedPlugins {

    public void setupSupportedPlugins() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            // Check if PlaceholderAPI is installed.
            Bukkit.getConsoleSender().sendMessage("PlaceholderAPI found!");
            new PlaceHolderAPI(CustomJavaPlugin.getPlugin(Main.class)).register();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabling the PlaceHolders! - Using PlaceHolder PlaceholderExpansion.");
        }
    }

}
