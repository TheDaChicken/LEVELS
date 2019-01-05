package level.plugin.Libs;

import org.bukkit.entity.Player;

public interface Lib {

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut);

    public void sendTabHF(Player player, String header, String footer);

    public void sendActionBar(Player p, String message);

}
