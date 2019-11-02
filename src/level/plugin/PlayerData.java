package level.plugin;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    UUID player_uuid = null;
    Player player_object = null;
    String player_name = null;

    public PlayerData(Player player_object) {
        this.player_object = player_object;
        this.player_uuid = player_object.getUniqueId();
    }
}
