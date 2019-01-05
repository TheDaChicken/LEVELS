package level.plugin.Commands;

import level.plugin.API.LevelsPlugin;
import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.PlayerData;
import level.plugin.StorageOptions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugLevel implements CommandExecutor {

    public DebugLevel(Main main) {

    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("debuglevel")) {
            if (sender.isOp()) {
                Player player = (Player) sender;
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Level: " + Main.playerData.get(player).getLevel());
                    sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "levelstring: " + Main.playerData.get(player).getLevelString());
                    sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "maxLevel: " + Main.playerData.get(player).getMaxLevel());
                    sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "maxpoints: " + Main.playerData.get(player).getMaxPoints());
                    sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "maxprefixnumber: " + Main.playerData.get(player).MaxPrefixNumber);
                    sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "points: " + Main.playerData.get(player).getPoints());
                    if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
                        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "StorageType: " + "FILE");
                    }
                    if (StorageOptions.isStorageOption(StorageOptions.MYSQL)) {
                        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "StorageType: " + "MYSQL");
                    }
                } else {
                    if (args[0].equalsIgnoreCase("api")) {
                        if (args.length == 1) {
                            sender.sendMessage("Usage: /debuglevel api <username>");
                            return true;
                        } else {
                            String username = args[1];
                            if (Bukkit.getOfflinePlayer(username) != null || Bukkit.getOfflinePlayer(username).hasPlayedBefore()) {
                                OfflinePlayer a = Bukkit.getOfflinePlayer(username);
                                sender.sendMessage("Username: " + a.getName());
                                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Level: " + LevelsPlugin.getPlayersLevel(a));
                                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "levelstring: " + LevelsPlugin.getPlayersLevelString(a));
                                //sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "maxLevel: " + "");
                                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "maxpoints: " + LevelsPlugin.getPlayersMaxPoints(a));
                                //sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "maxprefixnumber: " + "");
                                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "points: " + LevelsPlugin.getPlayersPoints(a));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("position")) {
                        if (Main.playerData.get(player) == null) {
                            Main.playerData.put(player, new PlayerData(player));
                        }
                        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "Position: " + Main.playerData.get(player).getLeaderboardPosition() + "");
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("placeholderapi")) {
                        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            if (args.length == 1) {
                                String message = "level_string: %levels_level_string%\nlevel_number: %levels_level_number%\npoints: %levels_points% \npoints_max: %levels_points_max%\n";
                                String withPlaceholdersSet = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
                                player.sendMessage(withPlaceholdersSet);
                            } else {
                                if (args[1].equalsIgnoreCase("PositionTest")) {
                                    String message = "Your Position: %levels_position%\nLeaderboard:\n%levels_position_username:1%: %levels_position_level:1%";
                                    String withPlaceholdersSet = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
                                    player.sendMessage(withPlaceholdersSet);
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "You can't use placeholderapi placeholders if you don't have it placeholderapi installed!");
                        }
                    }
                }
            } else {
                sender.sendMessage(Messages.YouNeedOP);
            }
            return true;
        }
        return false;
    }

}
