package level.plugin.Commands;

import level.plugin.Events.onDeath;
import level.plugin.Main;
import level.plugin.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class levelreload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("levelreload")) {
            Player player_sender = null;
            if (sender instanceof Player) {
                player_sender = (Player) sender;
            }
            if (args.length == 0) {
                Messages.reloadMessages();
                sender.sendMessage(Messages.MessagereloadedSucessful(player_sender));
                File Config = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "levelsconfig.yml");
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(Config);
                if (onDeath.mob_list_config_cache != null) {
                    onDeath.reloadMobListConfigCache();
                    sender.sendMessage(Messages.MobListConfigreloadedSucessful(player_sender));
                }
            } else {
                sender.sendMessage(Messages.DoesntContainSubCommands(player_sender));
            }
            return true;
        }
        return false;
    }
}
