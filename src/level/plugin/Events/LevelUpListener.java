package level.plugin.Events;

import level.plugin.CustomEvents.LevelUpEvent;
import level.plugin.CustomJavaPlugin;
import level.plugin.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LevelUpListener implements Listener {

    @EventHandler
    public void LevelUpEvent(LevelUpEvent event) {
        Player player = event.getPlayer();
        int level = event.getLevel();

        CustomJavaPlugin javaPlugin = CustomJavaPlugin.getPlugin(Main.class);
        FileConfiguration ymlconfig = javaPlugin.getConfig();
    }

}
