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
            if (sender.isOp()) {
                if (args.length == 0 || args.length == 1) {
                    sender.sendMessage(Messages.AddPointsUsage);
                    return true;
                }
                String username = args[0];
                String number = args[1];
			
/*			if(Bukkit.getPlayer(username) == null) {
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "That Player is not online!");
			} else {*/

            //^^ WHAT IS THIS?

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
                        sender.sendMessage(Messages.ProblemAddingPoints);
                        e.printStackTrace();
                        return true;
                    }
                    try {
                        Main.playerData.get(player).Addpoints(integer);
                    } catch (MaxLevel e) {
                        //e.printStackTrace();
                        player.sendMessage(Messages.AddPointsMaxLevelCatchMessage);
                    }
                    return true;
                } else {
                    OfflinePlayer offlinePlayer;
                    int integer;

                    try {
                        offlinePlayer = Main.getOfflinePlayer(username);
                    } catch (TheUserhasNotplayedBefore e) {
                        sender.sendMessage(Messages.PlayerhasNotJoinedServerBefore);
                        return true;
                    }

                    try {
                        integer = Integer.valueOf(number);
                    } catch (Exception e) {
                        sender.sendMessage(Messages.ProblemAddingPoints);
                        e.printStackTrace();
                        return true;
                    }
                    try {
                        OfflinePlayerMethods.Addpoints(offlinePlayer, integer);
                    } catch (MaxLevel e) {
                        return true;
                    }
                }

            } else {
                sender.sendMessage(Messages.YouNeedOP);
                return true;
            }

        }
        return false;
    }

}
