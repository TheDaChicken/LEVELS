package level.plugin;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public abstract class CustomJavaPlugin extends JavaPlugin {

    /*
        Looked at, https://bukkit.gamepedia.com/Configuration_API_Reference
     */

    private FileConfiguration dataFC = null;
    private File dataFile = new File(this.getDataFolder(), "data.yml");

    private FileConfiguration messageFC = null;
    private File messageFile = new File(this.getDataFolder(), "messages.yml");

    private FileConfiguration mobConfigFC = null;
    private File mobConfigFile = new File(this.getDataFolder(), "moblistconfig.yml");

    FileConfiguration getDataFile() {
        if (this.dataFC == null) {
            this.reloadData();
        }
        return this.dataFC;
    }

    FileConfiguration getMessageFile() {
        if (this.messageFC == null) {
            this.reloadMessages();
        }
        return this.messageFC;
    }

    public FileConfiguration getMobConfig() {
        if (this.mobConfigFC == null) {
            this.reloadMobConfig();
        }
        return this.mobConfigFC;
    }

    private void reloadData() {
        this.dataFC = YamlConfiguration.loadConfiguration(this.dataFile);
    }

    private void reloadMobConfig() {
        this.mobConfigFC = YamlConfiguration.loadConfiguration(this.mobConfigFile);
    }

    private void reloadMessages() {
        this.messageFC = YamlConfiguration.loadConfiguration(this.messageFile);
        InputStream defaultMessages = this.getResource("messages.yml");
        if (defaultMessages != null) {
            messageFC.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaultMessages, Charsets.UTF_8)));
        }
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
            this.getMessageFile().save(this.messageFile);
        } catch (IOException var2) {
            this.getLogger().log(Level.SEVERE, "Could not save messages to " + this.messageFile, var2);
        }
    }

    public void saveDefaultMessages() {
        if (!this.messageFile.exists()) {
            this.saveResource("messages.yml", false);
        }

    }
}
