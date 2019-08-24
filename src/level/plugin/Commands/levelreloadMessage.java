package level.plugin.Commands;

import level.plugin.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class levelreloadMessage implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("levelreloadmessages")) {
            Player player_sender = null;
            if (sender instanceof Player) {
                player_sender = (Player) sender;
            }
            if (args.length == 0) {
                Messages.reloadMessages();
                sender.sendMessage(Messages.MessagereloadedSucessful(player_sender));
            } else {
                sender.sendMessage(Messages.DoesntContainSubCommands(player_sender));
            }
            return true;
        }
        return false;
    }
}
