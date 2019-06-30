package level.plugin.Events;

import level.plugin.Errors.MaxLevel;
import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.PlayerData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class onDeath implements Listener {
    public onDeath(Main main) {
    }
    @EventHandler
    public void Death(EntityDeathEvent event) {
        File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "moblistconfig.yml");
        YamlConfiguration Configcfg = YamlConfiguration.loadConfiguration(Config);
        File l = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(l);
        if (yml.getBoolean("EnableKillMobsPoints")) {
            if (Config.exists()) {
                Player player = event.getEntity().getKiller();
                String entityName = event.getEntity().getType().getName();
                if (player != null) {
                    boolean isThere = Configcfg.get("mobs." + entityName) != null;
                    if (isThere) {
                        int points = Configcfg.getInt("mobs." + entityName);
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
