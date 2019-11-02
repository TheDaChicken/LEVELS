package level.plugin.Events;

import level.plugin.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    public void onQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Main.onlinePlayers.remove(player);
    }

}
