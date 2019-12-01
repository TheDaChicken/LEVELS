package level.plugin.CustomEvents;

import level.plugin.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LevelUpEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private int Level;
    private boolean isCancelled;
    private PlayerData playerData;

    public LevelUpEvent(Player player, int Level, PlayerData playerData) {
        this.player = player;
        this.Level = Level;
        this.playerData = playerData;
        this.isCancelled = false;
    }

    public int getLevel() {
        return Level;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}