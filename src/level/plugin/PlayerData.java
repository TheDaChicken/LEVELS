package level.plugin;

import level.plugin.Enums.StorageOptions;
import level.plugin.Exceptions.Player.PlayerNameDoesntExist;
import level.plugin.Exceptions.Player.PlayerNotPlayedBefore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class PlayerData {

    boolean player_online = true;

    private UUID player_uuid = null;
    private Player player_object = null;
    private String player_name = null;

    private Integer level, points = 0;

    public PlayerData(Player player_object) {
        this.player_object = player_object;
        this.player_uuid = player_object.getUniqueId();
        this.player_name = player_object.getName();
        loadPlayerData();
    }

    @SuppressWarnings("deprecation")
    PlayerData(String player_name) throws PlayerNotPlayedBefore, PlayerNameDoesntExist {
        /*
         Creates PlayerData class from player's name.
         @throws PlayerNotPlayedBefore, PlayerNameDoesntExist.
         */

        Player player = Bukkit.getPlayer(player_name);
        this.player_name = player_name;
        if (player != null) {
            this.player_object = player;
            this.player_uuid = player.getUniqueId();
        } else {
            OfflinePlayer offline_player = Bukkit.getOfflinePlayer(this.player_name);
            if (offline_player == null) {
                throw (new PlayerNameDoesntExist());
            } else if (!offline_player.hasPlayedBefore()) {
                throw (new PlayerNotPlayedBefore());
            }
            this.player_uuid = Bukkit.getOfflinePlayer(player_name).getUniqueId();
        }
        loadPlayerData();
    }

    private void loadPlayerData() {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            CustomJavaPlugin plugin = CustomJavaPlugin.getPlugin(Main.class);
            if (!plugin.getDataFile().contains("Users." + this.player_uuid.toString() + ".level")) {
                plugin.getDataFile().set("Users." + this.player_uuid.toString() + ".level", 0);
                plugin.getDataFile().set("Users." + this.player_uuid.toString() + ".points", 0);
                plugin.saveDataFile();
            }

            this.level = plugin.getDataFile().getInt("Users." + this.player_uuid.toString() + ".level");
            this.points = plugin.getDataFile().getInt("Users." + this.player_uuid.toString() + ".points");
        }
    }

    UUID getPlayerUUID() {
        return this.player_uuid;
    }

    String getPlayerName() {
        return this.player_name;
    }

    Player getPlayerObject() {
        return this.player_object;
    }

    int getLevel() {
        return this.level;
    }

    public boolean setLevel(int level) {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            CustomJavaPlugin plugin = CustomJavaPlugin.getPlugin(Main.class);
            plugin.getDataFile().set("Users." + this.player_uuid.toString() + ".level", level);
            plugin.saveDataFile();
            this.level = level;
            return true;
        }
        return false;
    }

    public boolean setPoints(int points) {
        if (StorageOptions.isStorageOption(StorageOptions.FILE)) {
            CustomJavaPlugin plugin = CustomJavaPlugin.getPlugin(Main.class);
            plugin.getDataFile().set("Users." + this.player_uuid.toString() + ".points", points);
            plugin.saveDataFile();
            this.points = points;
            return true;
        }
        return false;
    }

    int getPoints() {
        return this.points;
    }

    String getLevelPrefix() {
        CustomJavaPlugin plugin = CustomJavaPlugin.getPlugin(Main.class);
        Set<String> storedLevelPrefixKeys = plugin.getConfig().getConfigurationSection("LevelPrefix").getKeys(false);
        if (storedLevelPrefixKeys.contains(String.valueOf(this.level))) {
            return plugin.getConfig().getString("LevelPrefix." + this.level);
        } else {
            Integer best_number = null;
            for (String stored_level : storedLevelPrefixKeys) {
                Integer number = Main.convertStringToInt(stored_level);
                if (number == null) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "'\'" + stored_level + "\' is not a number. Please check Level's config!");
                } else if (number < this.level) {
                    best_number = number;
                }
            }
            if (best_number != null) {
                return plugin.getConfig().getString("LevelPrefix." + best_number);
            }
        }

        return "";
    }

    public int getMaxPoints() {
        return 20;
    }

    public void addPoints(int points) {
        int max_points = getMaxPoints();
        int points_amount = Math.abs(this.points + points);
        if (points_amount == max_points) {
            if (!setPoints(0)) {
                //player.sendMessage(Messages.StoragePlaceNotWorking);
            }
            if (!setLevel(this.level + 1)) {
                //player.sendMessage(Messages.StoragePlaceNotWorking);
            }
        } else if (points_amount < max_points) {
            if (!setPoints(points_amount)) {
                //player.sendMessage(Messages.StoragePlaceNotWorking);
            }
        } else {

        }
    }

}
