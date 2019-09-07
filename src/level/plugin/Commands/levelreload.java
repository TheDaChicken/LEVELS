package level.plugin.Commands;

import level.plugin.Events.onDeath;
import level.plugin.Messages;
import level.plugin.configHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                if (onDeath.mob_list_config_cache != null) {
                    onDeath.reloadMobListConfigCache();
                    sender.sendMessage(Messages.MobListConfigreloadedSucessful(player_sender));
                }
                configHandler.refreshLevelConfigCache();
                sender.sendMessage(Messages.LevelConfigreloadedSucessful(player_sender));
            } else {
                sender.sendMessage(Messages.DoesntContainSubCommands(player_sender));
            }
            return true;
        }
        return false;
    }
}
