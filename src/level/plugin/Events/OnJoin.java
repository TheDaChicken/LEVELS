package level.plugin.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import level.plugin.Main;
import level.plugin.PlayerData;

public class OnJoin implements Listener {

	@EventHandler
	public void Join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		//Adds the player as a temp.
		Main.playerData.put(player, new PlayerData(player));
	}
}
