package level.plugin.Events;

import level.plugin.Errors.MaxLevel;
import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.PlayerData;
import level.plugin.configHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class onDeath implements Listener {

    public static YamlConfiguration mob_list_config_cache;

    public static void reloadMobListConfigCache() {
        mob_list_config_cache = YamlConfiguration.loadConfiguration(
                new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "moblistconfig.yml"));
    }


    @EventHandler
    public void Death(EntityDeathEvent event) {
        YamlConfiguration yml = configHandler.yamlConfiguration();
        Player player = event.getEntity().getKiller();
        if (event.getEntity().getType() == EntityType.PLAYER) {
            if (yml.getBoolean("EnablePlayerPoints")) {
                int points = yml.getInt("PlayerPointsAmount");
                if (Main.playerData.get(player) == null) {
                    Main.playerData.put(player, new PlayerData(player));
                }
                try {
                    Main.playerData.get(player).Addpoints(points);
                } catch (MaxLevel maxLevel) {
                    player.sendMessage(Messages.AddPointsMaxLevelCatchMessage(player));
                }
            }
        } else {
            if (yml.getBoolean("EnableKillMobsPoints")) {
                File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "moblistconfig.yml");
                if (Config.exists()) {
                    if (mob_list_config_cache == null) {
                        mob_list_config_cache = YamlConfiguration.loadConfiguration(Config);
                    }
                    //Other Mobs
                    String entityName = event.getEntity().getType().getName().toUpperCase();
                    Bukkit.broadcastMessage(entityName);
                    if (player != null) {
                        if (mob_list_config_cache.contains("mobs." + entityName)) {
                            int points = mob_list_config_cache.getInt("mobs." + entityName);
                            // LivingEntity has a getKiller() method
                            //entity.getKiller().sendMessage("You killed an entity!");
                            // getKiller() returns a Player, so you don't have to check if it's a player.
                            if (Main.playerData.get(player) == null) {
                                Main.playerData.put(player, new PlayerData(player));
                            }
                            try {
                                Main.playerData.get(player).Addpoints(points);
                            } catch (MaxLevel maxLevel) {
                                player.sendMessage(Messages.AddPointsMaxLevelCatchMessage(player));
                            }
                        }
                    }
                }
            }
        }
    }
}
