package level.plugin.SupportedPluginsClasses;

import com.nametagedit.plugin.NametagEdit;
import org.bukkit.entity.Player;

public class NameTagEdit {

    public static void setPrefix(Player player, String text) {
        NametagEdit.getApi().setPrefix(player, text);
    }

    public static void setSuffix(Player player, String text) {
        NametagEdit.getApi().setSuffix(player, text);
    }

}
