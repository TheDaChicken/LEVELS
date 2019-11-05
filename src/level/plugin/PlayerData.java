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
            if (!CustomJavaPlugin.getPlugin(Main.class).getDataFile().contains("Users." + this.player_uuid.toString() + ".level")) {
                CustomJavaPlugin.getPlugin(Main.class).getDataFile().set("Users." + this.player_uuid.toString() + ".level", 0);
                CustomJavaPlugin.getPlugin(Main.class).getDataFile().set("Users." + this.player_uuid.toString() + ".points", 0);
                CustomJavaPlugin.getPlugin(Main.class).saveDataFile();
            }

            this.level = CustomJavaPlugin.getPlugin(Main.class).getDataFile().getInt("Users." + this.player_uuid.toString() + ".level");
            this.points = CustomJavaPlugin.getPlugin(Main.class).getDataFile().getInt("Users." + this.player_uuid.toString() + ".points");
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

    public int getLevel() {
        return this.level;
    }

    public int getPoints() {
        return this.points;
    }

    public void setLevel(int level) {


    }

}
