package level.plugin.Events;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import level.plugin.Main;
import org.bukkit.plugin.java.JavaPlugin;

public class OnChat implements Listener {
	public OnChat(Main main) {
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void Chat(AsyncPlayerChatEvent event) {
        File l = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(l);
		Player player = event.getPlayer();
		String chat = event.getFormat();

        String levelstring = Main.playerData.get(player).getLevelString();
        String levelprefix = Main.playerData.get(player).getLevelPrefix();
        String levelnumber = String.valueOf(Main.playerData.get(player).getLevel());

		String level = ChatColor.translateAlternateColorCodes('&', yml.getString("ForceChatFormat")).replace("%levelstring%", levelstring).replace("%level%", levelnumber).replace("%levelprefix%", levelprefix);

		if (yml.getBoolean("Force Level Prefix to any chat plugin")) {
		
		    if(level != null) {

		        event.setFormat("" + level + "" + chat);

		    }
		
		}
		
	}
	
}
