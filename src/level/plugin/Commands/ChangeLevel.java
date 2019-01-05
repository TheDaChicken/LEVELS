package level.plugin.Commands;

import level.plugin.Errors.MaxLevel;
import level.plugin.Errors.TheUserhasNotplayedBefore;
import level.plugin.Errors.TheUserisNotOnline;
import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.OfflinePlayerMethods;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangeLevel implements CommandExecutor {

    public ChangeLevel(Main main) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("changelevel")) {
            if (sender.isOp()) {
                if (args.length == 0 || args.length == 1) {
                    sender.sendMessage(Messages.ChangeLevelUsage);
                    return true;
                } else {
                    String username = args[0];
                    String number = args[1];
                    boolean online = Main.isPlayerOnline(username);

                    if (online) {
                        Player player;
                        int parseint;
                        try {
                            player = Main.getPlayerbyString(username);
                        } catch (TheUserisNotOnline theUserisNotOnline) {
                            theUserisNotOnline.printStackTrace();
                            return true;
                        }
                        try {
                            parseint = Integer.parseInt(number);
                        } catch (Exception e) {
                            sender.sendMessage(Messages.ProblemChangingLevel);
                            return true;
                        }
                        try {
                            Main.playerData.get(player).ChangeLevel(parseint);
                            sender.sendMessage(Messages.LevelHasSet);
                            return true;
                        } catch (MaxLevel e) {
                            //e.printStackTrace();
                            sender.sendMessage(Messages.LevelHigherThenMaxLevel);
                            return true;
                        }
                    } else {
                        OfflinePlayer offlinePlayer;
                        int parseint;

                        try {
                            offlinePlayer = Main.getOfflinePlayer(username);
                        } catch (TheUserhasNotplayedBefore e) {
                            sender.sendMessage(Messages.PlayerhasNotJoinedServerBefore);
                            return true;
                        }
                        try {

                            parseint = Integer.parseInt(number);
                        } catch (Exception e) {
                            sender.sendMessage(Messages.ProblemChangingLevel);
                            return true;
                        }
                        try {
                            OfflinePlayerMethods.ChangeLevel(offlinePlayer, parseint);
                        } catch (MaxLevel maxLevel) {
                            sender.sendMessage(Messages.LevelHigherThenMaxLevel);
                            return true;
                        }
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
