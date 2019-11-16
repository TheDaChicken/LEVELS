package level.plugin.Events;

import level.plugin.CustomEvents.LevelUpEvent;
import level.plugin.CustomJavaPlugin;
import level.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class LevelUpListener implements Listener {

    @EventHandler
    public void LevelUpEvent(LevelUpEvent event) {
        Player player = event.getPlayer();
        int level = event.getLevel();

        CustomJavaPlugin javaPlugin = CustomJavaPlugin.getPlugin(Main.class);
        FileConfiguration ymlconfig = javaPlugin.getConfig();

        if (ymlconfig.getBoolean("levelupruncommand")) {
            if (ymlconfig.getBoolean("levelupruncommandcertainperlevel")) {
                if (ymlconfig.getStringList("levelcommands." + level) == null) {
                    return;
                }
                List<String> commands = ymlconfig.getStringList("levelcommands." + level);
                for (String command : commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }
            } else {
                List<String> commands = ymlconfig.getStringList("levelupruncommandslist");
                for (String command : commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }
            }
        }

    }

}
