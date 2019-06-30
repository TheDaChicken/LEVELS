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

public class AddLevel implements CommandExecutor {

    public AddLevel(Main main) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("AddLevel")) {
            Player player_sender = null;
            if (sender instanceof Player) {
                player_sender = (Player) sender;
            }
            if (sender.isOp()) {
                if (args.length == 0 || args.length == 1) {
                    sender.sendMessage(Messages.AddLevelUsage(player_sender));
                    return true;
                } else {
                    String username = args[0];
                    String number = args[1];
                    boolean online = Main.isPlayerOnline(username);

                    if (online) {
                        Player player;
                        int integer;

                        try {
                            integer = Integer.valueOf(number);
                        } catch (Exception e) {
                            sender.sendMessage(Messages.ProblemAddingLevel(player_sender));
                            return true;
                        }

                        try {
                            player = Main.getPlayerbyString(username);
                        } catch (TheUserisNotOnline theUserisNotOnline) {
                            theUserisNotOnline.printStackTrace();
                            return true;
                        }
                        try {
                            Main.playerData.get(player).AddLevel(integer);
                        } catch (MaxLevel e) {
                            //e.printStackTrace();
                            sender.sendMessage(Messages.AddLevelMaxLevelCatchMessage(player_sender));
                            return true;
                        }
                        return true;

                    } else {
                        OfflinePlayer offlinePlayer = null;
                        int integer;

                        try {
                            integer = Integer.valueOf(number);
                        } catch (Exception e) {
                            sender.sendMessage(Messages.ProblemAddingLevel(player_sender));
                            return true;
                        }

                        try {
                            offlinePlayer = Main.getOfflinePlayer(username);
                        } catch (TheUserhasNotplayedBefore theUserhasNotplayedBefore) {
                            theUserhasNotplayedBefore.printStackTrace();
                        }
                        try {
                            OfflinePlayerMethods.AddLevel(offlinePlayer, integer);
                        } catch (MaxLevel e) {
                            //e.printStackTrace();
                            sender.sendMessage(Messages.AddLevelMaxLevelCatchMessage(player_sender));
                            return true;
                        }
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
