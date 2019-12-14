package level.plugin.Events;

import level.plugin.CustomJavaPlugin;
import level.plugin.Main;
import level.plugin.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Create Player Data
        PlayerData playerData = new PlayerData(player);
        // Update Player's NameTag. (Only works if enabled)
        Bukkit.getScheduler().scheduleAsyncDelayedTask(CustomJavaPlugin.getPlugin(Main.class), playerData::UpdateNameTag, 15L);
        // Add to HashMap
        Main.onlinePlayers.put(player, playerData);
    }

}
