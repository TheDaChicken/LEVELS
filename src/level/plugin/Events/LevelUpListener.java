package level.plugin.Events;

import level.plugin.CustomEvents.LevelUpEvent;
import level.plugin.CustomJavaPlugin;
import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class LevelUpListener implements Listener {

    Integer runnable = null;

    @EventHandler
    public void LevelUpEvent(LevelUpEvent event) {
        Player player = event.getPlayer();
        int level = event.getLevel();
        PlayerData playerData = event.getPlayerData();

        CustomJavaPlugin javaPlugin = CustomJavaPlugin.getPlugin(Main.class);
        FileConfiguration ymlconfig = javaPlugin.getConfig();

        if (ymlconfig.getBoolean("levelupruncommand")) {
            if (ymlconfig.getBoolean("levelupruncommandcertainperlevel")) {
                if (ymlconfig.getStringList("levelcommands." + level) == null) {
                    return;
                }
                List<String> commands = ymlconfig.getStringList("levelcommands." + level);
                for (String command : commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }
            } else {
                List<String> commands = ymlconfig.getStringList("levelupruncommandslist");
                for (String command : commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }
            }
        }
        playerData.UpdateNameTag();
        if (Main.lib != null) {
            runnable = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), new Runnable() {
                int timer = 10;

                public void run() {
                    if (timer != 0) {
                        Main.lib.sendActionBar(player, Messages.getMessage(playerData, "LevelUpActionbar"));
                        timer--;
                    } else {
                        Bukkit.getScheduler().cancelTask(runnable);
                    }
                }
            }, 10L, 10L);
        } else {
            player.sendMessage(Messages.getMessage(playerData, "LevelUpActionbar"));
        }
    }

}
