package level.plugin.Commands;

import level.plugin.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangeLevel implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("changelevel")) {
            Player player = getSenderPlayer(sender);
            if (args.length < 2) {
                sender.sendMessage(Messages.getMessage(player, "ChangeLevelUsage"));
            } else {
                String player_to_string = args[0];
                int number_result = 0;
                try {
                    number_result = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {

                    return true;
                }
            }
            return true;
        }
        return false;
    }

    private Player getSenderPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        return null;
    }
}
