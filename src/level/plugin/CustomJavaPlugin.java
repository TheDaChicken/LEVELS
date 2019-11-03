package level.plugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public abstract class CustomJavaPlugin extends JavaPlugin {

    /*
    Taken from Spigot's JavaPlugin class and been edited to add getDataFile. >_>
    I hope I can do that.
     */

    private FileConfiguration newData = null;
    private File dataFile = null;

    FileConfiguration getDataFile() {
        if (this.newData == null) {
            this.reloadData();
        }

        return this.newData;
    }

    private void reloadData() {
        this.newData = YamlConfiguration.loadConfiguration(this.dataFile);
    }

    void saveDataFile() {
        try {
            this.getDataFile().save(this.dataFile);
        } catch (IOException var2) {
            this.getLogger().log(Level.SEVERE, "Could not save data to " + this.dataFile, var2);
        }
    }

}
