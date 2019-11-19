package level.plugin.Commands;

import level.plugin.Exceptions.Player.PlayerNameDoesntExist;
import level.plugin.Exceptions.Player.PlayerNotPlayedBefore;
import level.plugin.Main;
import level.plugin.Messages;
import level.plugin.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class LevelStats implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("levelstats")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    sender.sendMessage(Messages.getMessage(player, "StatsInfoOnlySelf"));
                } else {
                    sender.sendMessage(Messages.getMessage((Player) null, "ConsoleLevelStatsUsage"));
                }
                return true;
            } else {
                Player player = getSenderPlayer(sender);
                String player_name = args[0];
                try {
                    PlayerData playerData = Main.getPlayerData(player_name);
                    sender.sendMessage(Messages.getMessage(playerData, "StatsInfoPlayers"));
                } catch (PlayerNameDoesntExist playerNameDoesntExist) {
                    HashMap<String, String> extra_hashmap = new HashMap<>();
                    extra_hashmap.put("player_to", player_name);
                    sender.sendMessage(Messages.getMessage(player, "PlayerNotExist", extra_hashmap));
                    return true;
                } catch (PlayerNotPlayedBefore playerNotPlayedBefore) {
                    HashMap<String, String> extra_hashmap = new HashMap<>();
                    extra_hashmap.put("player_to", player_name);
                    sender.sendMessage(Messages.getMessage(player, "PlayerNotPlayedBefore", extra_hashmap));
                    return true;
                }
                return true;
            }
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
