package level.plugin.Commands;

import level.plugin.OfflinePlayerMethods;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.Errors.CantChangeThatManyPoints;
import level.plugin.Errors.MaxLevel;
import level.plugin.Errors.TheUserhasNotplayedBefore;
import level.plugin.Errors.TheUserisNotOnline;

public class ChangePoints implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("changepoints")) {
            Player player_sender = null;
            if (sender instanceof Player) {
                player_sender = (Player) sender;
            }
            if (sender.isOp()) {
                if (args.length == 0 || args.length == 1) {
                    sender.sendMessage(Messages.ChangePointsUsage(player_sender));
                    return true;
                } else {
                    String username = args[0];
                    String number = args[1];
                    boolean online = Main.isPlayerOnline(username);

                    if (online) {
                        Player player;
                        try {
                            player = Main.getPlayerbyString(username);
                        } catch (TheUserisNotOnline e) { //
                            e.printStackTrace();
                            return true;
                        }
                        int integer;
                        try {
                            integer = Integer.valueOf(number);
                        } catch (Exception e) {
                            sender.sendMessage(Messages.ProblemAddingLevel(player_sender));
                            return true;
                        }
                        try {
                            Main.playerData.get(player).ChangePoints(integer);
                            return true;
                        } catch (MaxLevel e) {
                            player.sendMessage(Messages.ChangePointsMaxLevelCatchMessage(player));
                            return true;
                        } catch (CantChangeThatManyPoints e) {
                            sender.sendMessage(Messages.CantAddThatManyPoints(player_sender));
                            return true;
                        }
                    } else {
                        OfflinePlayer offlinePlayer;
                        int integer;

                        try {
                            offlinePlayer = Main.getOfflinePlayer(username);
                        } catch (TheUserhasNotplayedBefore e) {
                            sender.sendMessage(Messages.PlayerhasNotJoinedServerBefore(player_sender));
                            return true;
                        }
                        try {
                            integer = Integer.valueOf(number);
                        } catch (Exception e) {
                            sender.sendMessage(Messages.ProblemAddingLevel(player_sender));
                            return true;
                        }
                        try {
                            OfflinePlayerMethods.ChangePoints(offlinePlayer, integer);
                        } catch (MaxLevel e) {
                            //offlinePlayer.sendMessage(Messages.ChangePointsMaxLevelCatchMessage);
                            return true;
                        } catch (CantChangeThatManyPoints e) {
                            sender.sendMessage(Messages.CantAddThatManyPoints(player_sender));
                            return true;
                        }
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
