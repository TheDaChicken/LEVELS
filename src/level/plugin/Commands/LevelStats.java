package level.plugin.Commands;

import level.plugin.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelStats implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("levelstats")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    sender.sendMessage(Messages.getMessage(player, "StatsInfoOnlySelf"));
                } else {
                    sender.sendMessage(Messages.getMessage(null, "ConsoleLevelStatsUsage"));
                }
                return true;
            }
        }
        return false;
    }
}
