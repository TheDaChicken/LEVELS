package level.plugin.Leaderboard;

import level.plugin.Hologram;
import level.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LeaderboardHologram {

    public static Hologram leaderboard = null;

    public static void setupLeaderboardHologram(Location location) {

        leaderboard = new Hologram(location);

        leaderboard.ChangeLine(0, ChatColor.RED + "" + ChatColor.BOLD + "Setting up! Please Wait!");

        File Data = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
        if (!Data.exists()) {
            try {
                Data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Data);

        double X = location.getX();
        double Y = location.getY();
        double Z = location.getZ();

        String worldname = location.getWorld().getName();

        yml.set("hologram.location.worldname", worldname);
        yml.set("hologram.location.X", X);
        yml.set("hologram.location.Y", Y);
        yml.set("hologram.location.Z", Z);

        try {
            yml.save(Data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), () -> {
            UpdateLeaderboardHologramTimer();
        }, 20L);
    }

    public static void SpawnLeaderboardHologram() {
        File Data = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
        if (!Data.exists()) {
            try {
                Data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Data);

        if(leaderboard == null) {
            if (yml.getString("hologram.location.worldname") != null) {
                if(yml.getString("hologram.location.worldname").equalsIgnoreCase("[none]") != true) {
                    double X = (double) yml.getInt("hologram.location.X");
                    double Y = (double) yml.getInt("hologram.location.Y");
                    double Z = (double) yml.getInt("hologram.location.Z");
                    World world = Bukkit.getWorld(yml.getString("hologram.location.worldname"));
                    Location location = new Location(world, X, Y, Z);

                    leaderboard = new Hologram(location);
                    UpdateLeaderboardHologramTimer();
                }
            }
        }
    }

    public static void UpdateLeaderboardHologramTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
            UpdateLeaderboardHologram();
        }, 0L, 20L);
    }


    public static void UpdateLeaderboardHologram() {
        //Hologram.UpdateText(leaderboard, LeaderboardHandler.GetLeaderboardString());
        int loop = 0;
        for(String leaderboardline : LeaderboardHandler.GetLeaderboardArray()) {
            leaderboard.ChangeLine(loop, leaderboardline);
            loop++;
        }
    }


    public static void RemoveleaderboardHologram() {
        if(leaderboard != null) {
            leaderboard.Remove();
            leaderboard = null;
        }
    }


    public static boolean isDeleted() {
        File Data = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
        if (!Data.exists()) {
            try {
                Data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Data);
        if(leaderboard == null) {
            return true;
        }
        if (yml.getString("hologram.location.worldname") == null || yml.getString("hologram.location.worldname").equalsIgnoreCase("[none]") == true) {
            return true;
        }
        return false;
    }

    public static void DeleteLeaderboardHologram() {
        leaderboard.Remove();
        leaderboard = null;
        File Data = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");
        if (!Data.exists()) {
            try {
                Data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Data);
        yml.set("hologram.location.worldname", "[none]");
        try {
            yml.save(Data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
