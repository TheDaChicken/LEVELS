package level.plugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public abstract class CustomJavaPlugin extends JavaPlugin {

    /*
        Looked at Spigot's 1.8.9 Jar to get an example of what this class looks like.
     */

    private FileConfiguration dataFC = null;
    private File dataFile = new File(this.getDataFolder(), "data.yml");

    private FileConfiguration messageFC = null;
    private File messageFile = new File(this.getDataFolder(), "messages.yml");

    FileConfiguration getDataFile() {
        if (this.dataFC == null) {
            this.reloadData();
        }

        return this.dataFC;
    }

    FileConfiguration getMessageFile() {
        if (this.messageFile == null) {
            this.reloadData();
        }

        return this.messageFC;
    }

    private void reloadData() {
        this.dataFC = YamlConfiguration.loadConfiguration(this.dataFile);
    }

    private void reloadMessages() {
        this.messageFC = YamlConfiguration.loadConfiguration(this.messageFile);
    }

    void saveDataFile() {
        try {
            this.getDataFile().save(this.dataFile);
        } catch (IOException var2) {
            this.getLogger().log(Level.SEVERE, "Could not save data to " + this.dataFile, var2);
        }
    }

    void saveMessagesFile() {
        try {
            this.getDataFile().save(this.messageFile);
        } catch (IOException var2) {
            this.getLogger().log(Level.SEVERE, "Could not save data to " + this.messageFile, var2);
        }
    }

    public void saveDefaultMessages() {
        if (!this.messageFile.exists()) {
            this.saveResource("messages.yml", false);
        }

    }

}
