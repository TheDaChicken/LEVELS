package level.plugin.Leaderboard;

import level.plugin.Main;
import level.plugin.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeaderHeads {

    public static void SignPosition(Location location, int LevelSignPosition) {
        String worldname = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        File Data = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");

        if (!Data.exists()) {

            try {
                Data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Data);

        yml.set("LeaderHeads.Sign." + String.valueOf(LevelSignPosition) + ".world", worldname);

        yml.set("LeaderHeads.Sign." + String.valueOf(LevelSignPosition) + ".x", x);
        yml.set("LeaderHeads.Sign." + String.valueOf(LevelSignPosition) + ".y", y);
        yml.set("LeaderHeads.Sign." + String.valueOf(LevelSignPosition) + ".z", z);

        try {
            yml.save(Data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage("Added " + String.valueOf(LevelSignPosition) + " leaderhead sign to Data.yml! (Overwritten if it's already in there)");
    }

    public static ArrayList<LeaderHeadSign> getSignList() {
        File Data = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");

        if (!Data.exists()) {

            try {
                Data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Data);

        List<Integer> positions = new ArrayList<Integer>();

        ArrayList<LeaderHeadSign> signList = new ArrayList<LeaderHeadSign>();

        int max = LeaderboardHandler.getLowestLeaderboardPosition();
        int a = 0;

        while (a <= max)
        {
            String location = "LeaderHeads.Sign." + a + ".world";

            if(yml.getString(location) != null) {
                positions.add(a);
            }
            a++;
        }

        for(Integer position : positions) {

            World world = Bukkit.getWorld(yml.getString("LeaderHeads.Sign." + position + ".world"));
            double x = (double) yml.getInt("LeaderHeads.Sign." + position + ".x");
            double y = (double) yml.getInt("LeaderHeads.Sign." + position + ".y");
            double z = (double) yml.getInt("LeaderHeads.Sign." + position + ".z");

            Location location = new Location(world, x, y, z);

            Block block = location.getBlock();

            BlockState state = block.getState();
            if (!(state instanceof Sign)) {
                Bukkit.getConsoleSender().sendMessage("There is no sign anymore for position: " + position + " anymore. It has been removed from the data.yml");
                yml.set("LeaderHeads.Sign." + position, "");
            } else {
                Sign sign = (Sign) state;
                signList.add(new LeaderHeadSign(sign, position));
            }

            try {
                yml.save(Data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return signList;
    }

    public static boolean UpdateSigns() {
        ArrayList<LeaderHeadSign> signList = getSignList();

        if(signList.size() == 0) {
            return false;
        }


        for(LeaderHeadSign headSign : signList) {
            Sign sign = headSign.sign;
            int position = headSign.position;

            if(getPositionInfo(position).username.equalsIgnoreCase("null")) {

                int loop = 0;
                for(String string : Messages.SignMessageNoOneThere(position)) {
                    sign.setLine(loop, string);
                    loop++;
                }
            } else {
                PositionInfo positionInfo = getPositionInfo(position);

                int loop = 0;
                for(String string : Messages.SignMessage(positionInfo.username, positionInfo.position)) {
                    sign.setLine(loop, string);
                    loop++;
                }
            }
            sign.update();
        }


        return true;
    }

    public static PositionInfo getPositionInfo(int position) {
        ArrayList<PositionInfo> positionInfos = LeaderboardHandler.GetLeaderboardPositionInfo();
        try {
            return positionInfos.get(position - 1);
        } catch (Exception e) {
            return new PositionInfo("null", 0, 0);
        }
    }

    private static int scheduleSyncRepeatingTask = 0;

    public static void UpdateSignHeadScheduler() {
        scheduleSyncRepeatingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
            if(UpdateSigns() == false) {
                Bukkit.getScheduler().cancelTask(scheduleSyncRepeatingTask);
            }
            UpdateHeads();
        }, 0L, 20L);
    }

    public static void setupHead(Location location, int LevelHeadPosition) {

        String worldname = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        File Data = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");

        if (!Data.exists()) {

            try {
                Data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Data);

        yml.set("LeaderHeads.Head." + String.valueOf(LevelHeadPosition) + ".world", worldname);

        yml.set("LeaderHeads.Head." + String.valueOf(LevelHeadPosition) + ".x", x);
        yml.set("LeaderHeads.Head." + String.valueOf(LevelHeadPosition) + ".y", y);
        yml.set("LeaderHeads.Head." + String.valueOf(LevelHeadPosition) + ".z", z);

        try {
            yml.save(Data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage("Added " + String.valueOf(LevelHeadPosition) + " leaderhead head to Data.yml! (Overwritten if it's already in there)");
    }


    public static ArrayList<LeaderHeadHead> getHeadList() {
        File Data = new File(JavaPlugin.getPlugin(Main.class).getDataFolder().getPath(), "data.yml");

        if (!Data.exists()) {

            try {
                Data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(Data);

        List<Integer> positions = new ArrayList<Integer>();

        ArrayList<LeaderHeadHead> signList = new ArrayList<LeaderHeadHead>();

        int max = LeaderboardHandler.getLowestLeaderboardPosition();
        int a = 0;

        while (a <= max)
        {
            String location = "LeaderHeads.Head." + a + ".world";

            if(yml.getString(location) != null) {
                positions.add(a);
            }
            a++;
        }

        for(Integer position : positions) {

            World world = Bukkit.getWorld(yml.getString("LeaderHeads.Head." + position + ".world"));
            double x = (double) yml.getInt("LeaderHeads.Head." + position + ".x");
            double y = (double) yml.getInt("LeaderHeads.Head." + position + ".y");
            double z = (double) yml.getInt("LeaderHeads.Head." + position + ".z");

            Location location = new Location(world, x, y, z);

            Block block = location.getBlock();

            BlockState state = block.getState();
            if (!(state instanceof Skull)) {

            } else {
                Skull skull = (Skull) state;
                signList.add(new LeaderHeadHead(skull, position));
            }

            try {
                yml.save(Data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return signList;
    }

    public static boolean UpdateHeads() {
        ArrayList<LeaderHeadHead> signList = getHeadList();
        if(signList.size() == 0) {
            return false;
        }

        for(LeaderHeadHead headSign : signList) {
            Skull skull = headSign.skull;
            int position = headSign.position;

            if(getPositionInfo(position).username.equalsIgnoreCase("null")) {
                int loop = 0;
                skull.setOwner("steve");
            } else {
                PositionInfo positionInfo = getPositionInfo(position);
                skull.setOwner(positionInfo.username);
            }
            skull.update();
        }
        return true;
    }


}
