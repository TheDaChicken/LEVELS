package level.plugin.Events;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import level.plugin.Main;
import level.plugin.PlayerData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class OnJoin implements Listener {

	@EventHandler
	public void Join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		//Adds the player as a temp.
		Main.playerData.put(player, new PlayerData(player));
        File l = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(l);
        if (yml.contains("setLevels-To-Minecraft-Levels")) {
            if (yml.getBoolean("setLevels-To-Minecraft-Levels")) {
                	player.setLevel(Main.playerData.get(player).getLevel());
            }
        }
	}
}
