package level.plugin.Commands;

import level.plugin.Errors.TheUserhasNotplayedBefore;
import level.plugin.Errors.TheUserisNotOnline;
import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Stats implements CommandExecutor {

    public Stats(Main main) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("levelstats")) {
            if (sender instanceof Player) {
                if (args.length == 0) {
                    Player player = (Player) sender;
                    if (Main.playerData.get(player) == null) {
                        Main.playerData.put(player, new PlayerData(player));
                    }
                    for (String message : Messages.StatsInfoOnlySelf(player)) {
                        sender.sendMessage(message);
                    }
                    return true;
                } else {
                    String username = args[0];
                    boolean online = Main.isPlayerOnline(username);
                    if (online) {
                        Player player;
                        try {
                            player = Main.getPlayerbyString(username);
                        } catch (TheUserisNotOnline e) {
                            e.printStackTrace();
                            return true;
                        }
                        if (Main.playerData.get(player) == null) {
                            Main.playerData.put(player, new PlayerData(player));
                        }
                        for (String test : Messages.StatsInfoPlayers(player)) {
                            sender.sendMessage(test);
                        }
                        return true;
                    } else {
                        OfflinePlayer a;
                        try {
                            a = Main.getOfflinePlayer(username);
                        } catch (TheUserhasNotplayedBefore e) {
                            sender.sendMessage(Messages.PlayerhasNotJoinedServerBefore);
                            return true;
                        }
                        for (String test : Messages.StatsInfoPlayers(a)) {
                            sender.sendMessage(test);
                        }
                        return true;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You need to be a player!");
                return true;
            }
        }
        return false;
    }

}
