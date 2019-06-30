package level.plugin.Commands;

import level.plugin.OfflinePlayerMethods;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.Errors.MaxLevel;
import level.plugin.Errors.TheUserhasNotplayedBefore;
import level.plugin.Errors.TheUserisNotOnline;

public class AddPoints implements CommandExecutor {

    public AddPoints(Main main) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("AddPoints")) {
            Player player_sender = null;
            if (sender instanceof Player) {
                player_sender = (Player) sender;
            }
            if (sender.isOp()) {
                if (args.length == 0 || args.length == 1) {
                    sender.sendMessage(Messages.AddPointsUsage(player_sender));
                    return true;
                }
                String username = args[0];
                String number = args[1];

                boolean online = Main.isPlayerOnline(username);

                if (online) {
                    Player player;
                    int integer;
                    try {
                        player = Main.getPlayerbyString(username);
                    } catch (TheUserisNotOnline theUserisNotOnline) {
                        theUserisNotOnline.printStackTrace();
                        return true;
                    }
                    try {
                        integer = Integer.valueOf(number);
                    } catch (Exception e) {
                        sender.sendMessage(Messages.ProblemAddingPoints(player_sender));
                        e.printStackTrace();
                        return true;
                    }
                    try {
                        Main.playerData.get(player).Addpoints(integer);
                    } catch (MaxLevel e) {
                        player.sendMessage(Messages.AddPointsMaxLevelCatchMessage(player));
                    }
                    return true;
                } else {
                    OfflinePlayer offlinePlayer;
                    int integer;

                    try {
                        offlinePlayer = Main.getOfflinePlayer(username);
                    } catch (TheUserhasNotplayedBefore e) {
                        sender.sendMessage(Messages.PlayerhasNotJoinedServerBefore(null));
                        return true;
                    }

                    try {
                        integer = Integer.valueOf(number);
                    } catch (Exception e) {
                        sender.sendMessage(Messages.ProblemAddingPoints(player_sender));
                        return true;
                    }
                    try {
                        OfflinePlayerMethods.Addpoints(offlinePlayer, integer);
                    } catch (MaxLevel e) {
                        return true;
                    }
                }

            } else {
                sender.sendMessage(Messages.YouNeedOP(player_sender));
                return true;
            }

        }
        return false;
    }

}
