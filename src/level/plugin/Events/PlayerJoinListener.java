package level.plugin.Events;

import level.plugin.Main;
import level.plugin.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Add to playerData
        Main.onlinePlayers.put(player, new PlayerData(player));
    }

}
