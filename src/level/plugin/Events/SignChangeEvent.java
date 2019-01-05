package level.plugin.Events;

import level.plugin.Leaderboard.LeaderHeads;
import level.plugin.Messages;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SignChangeEvent implements Listener {
    @EventHandler
    public void ChangEvent(org.bukkit.event.block.SignChangeEvent event) {
        Player player = event.getPlayer();
        if(event.getLine(0).equalsIgnoreCase("[LEVEL]")) {
            String position = event.getLine(1);
            Location location = event.getBlock().getLocation();
            try {
                Integer.parseInt(position);
                LeaderHeads.SignPosition(location, Integer.parseInt(position));
                player.sendMessage(Messages.LeaderHeadSignCreated(position));
            } catch (Exception exception) {
                player.sendMessage(Messages.leaderHeadNotANumber(position));
            }
        }
    }
}
