package level.plugin.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import level.plugin.Main;

public class OnQuit implements Listener {
	@EventHandler
	public void Quit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Main.playerData.remove(player);
	}
}
