package level.plugin.Commands;

import level.plugin.Leaderboard.LeaderHeads;
import level.plugin.Leaderboard.LeaderboardHologram;
import level.plugin.Main;
import level.plugin.Messages;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelLeaderboardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player_sender = null;
        if (sender instanceof Player) {
            player_sender = (Player) sender;
        }
        if(cmd.getName().equalsIgnoreCase("levelleaderboard")) {
            if(args.length == 0) {
                sender.sendMessage(Messages.LevelHologramCommandUsage(player_sender));
                return true;
            } else {
                if(args[0].equalsIgnoreCase("spawnHologram")) {
                    if(sender instanceof Player) {
                        Player player = (Player) sender;
                        LeaderboardHologram.setupLeaderboardHologram(player.getLocation());
                    } else {
                        sender.sendMessage(Messages.YouNeedtoBePlayer(player_sender));
                    }
                    return true;
                } else {
                    if(args[0].equalsIgnoreCase("deletehologram")) {
                        if(!LeaderboardHologram.isDeleted()) {
                            LeaderboardHologram.DeleteLeaderboardHologram();
                            sender.sendMessage(Messages.DeleteHologram(player_sender));
                        } else {
                            sender.sendMessage(Messages.HologramAlreadyDeleted(player_sender));
                        }
                        return true;
                    } else {
                        if(args[0].equalsIgnoreCase("setupleaderheads")) {
                            if(sender instanceof Player) {
                                Player player = (Player) sender;
                                player.sendMessage(Messages.LeaderHeadsLabelSign());
                            } else {
                                sender.sendMessage(Messages.YouNeedtoBePlayer(player_sender));
                            }
                            return true;
                        } else {
                            if(args[0].equalsIgnoreCase("sethead")) {
                                if(sender instanceof Player) {
                                    if (args.length == 1) {
                                        sender.sendMessage(Messages.leaderboardSetHeadUsage);
                                    } else {
                                        String number = args[1];
                                        try {
                                            Integer.parseInt(number);
                                        } catch (Exception e) {
                                            sender.sendMessage(Messages.leaderboardSetHeadNotNumber(number));
                                            return true;
                                        }
                                        Location location = ((Player) sender).getLocation();

                                        LeaderHeads.setupHead(location, Integer.parseInt(number));
                                        sender.sendMessage(Messages.PlaceHead);
                                    }
                                } else {
                                    sender.sendMessage(Messages.YouNeedtoBePlayer(player_sender));
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
