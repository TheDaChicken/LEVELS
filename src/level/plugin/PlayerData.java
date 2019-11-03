package level.plugin;

import level.plugin.Enums.StorageOptions;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    private UUID player_uuid = null;
    private Player player_object = null;
    private String player_name = null;

    private Integer level, points = null;

    public PlayerData(Player player_object) {
        this.player_object = player_object;
        this.player_uuid = player_object.getUniqueId();
        this.player_name = player_object.getName();
        loadPlayerData();
    }

    private void loadPlayerData() {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {

        }
    }

    public UUID getPlayerUUID() {
        return this.player_uuid;
    }

    public String getPlayerName() {
        return this.player_name;
    }

    public Player getPlayerObject() {
        return this.player_object;
    }

    public void setLevel(int level) {


    }

}
