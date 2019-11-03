package level.plugin.Storage;

import level.plugin.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class FILE implements Storage {

    private YamlConfiguration yml = null;
    private File dataFile = null;

    private YamlConfiguration getYamlConfiguration() {
        File dataFolder = JavaPlugin.getPlugin(Main.class).getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        this.dataFile = new File(dataFolder, "data.yml");
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        this.yml = YamlConfiguration.loadConfiguration(this.dataFile);
        return this.yml;
    }

    private boolean saveYamlConfiguration(YamlConfiguration yml) {
        try {
            yml.save(this.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean setPoints(String uuid, int points) {
        YamlConfiguration yml = getYamlConfiguration();
        if (yml != null) {
            yml.set("Users." + uuid + ".points", points);
            return saveYamlConfiguration(yml);
        }
        return false;
    }

    @Override
    public boolean setLevel(String uuid, int level) {
        return false;
    }

    @Override
    public int getlevel(String uuid) {
        return 0;
    }

    @Override
    public int getPoints(String uuid) {
        return 0;
    }
}
