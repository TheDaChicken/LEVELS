package level.plugin.Events;

import level.plugin.CustomJavaPlugin;
import level.plugin.Main;
import level.plugin.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onDeathEvent(EntityDeathEvent event) {
        CustomJavaPlugin plugin = CustomJavaPlugin.getPlugin(Main.class);
        FileConfiguration yml = plugin.getConfig();
        Player player = event.getEntity().getKiller();
        if (event.getEntity().getType() == EntityType.PLAYER) {
            if (yml.getBoolean("KillEntities.EnablePlayerPoints")) {
                int points = yml.getInt("KillEntities.PlayerPointsAmount");
                if (Main.onlinePlayers.get(player) == null) {
                    Main.onlinePlayers.put(player, new PlayerData(player));
                }
                Main.onlinePlayers.get(player).addPoints(points);
            }
        } else if (yml.getBoolean("KillEntities.EnableMobsPoints")) {
            FileConfiguration mobList = plugin.getMobConfig();
            String entityName = event.getEntity().getType().getName();
            if (player != null) {
                if (mobList.contains("mobs." + entityName.toUpperCase())) {
                    int points = mobList.getInt("mobs." + entityName.toUpperCase());
                    if (Main.onlinePlayers.get(player) == null) {
                        Main.onlinePlayers.put(player, new PlayerData(player));
                    }
                    Main.onlinePlayers.get(player).addPoints(points);
                }
            }
        }
    }
}
