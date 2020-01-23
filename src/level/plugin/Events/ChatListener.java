package level.plugin.Events;

import level.plugin.CustomJavaPlugin;
import level.plugin.Main;
import level.plugin.Messages;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.HashMap;

public class ChatListener implements Listener {

    @EventHandler
    public void ChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        CustomJavaPlugin plugin = CustomJavaPlugin.getPlugin(Main.class);
        ConfigurationSection yml = plugin.getConfig().getConfigurationSection("Chat");
        if(yml.getBoolean("Whole.Enable")) {
            HashMap<String, String> extra_hashmap = new HashMap<>();
            extra_hashmap.put("message", event.getMessage());
            String format = Messages.PlaceHolderString(player, yml.getString("Whole.Format"), extra_hashmap);
            event.setFormat(format);
        } else if(yml.getBoolean("Force.Enable")) {
            String format = Messages.PlaceHolderString(player, yml.getString("Force.Format"));
            event.setFormat(format + event.getFormat());
        }
    }
}
