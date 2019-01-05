package level.plugin.SupportedPluginsClasses;

import level.plugin.Main;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Vault {

    public static boolean isVaultInstalled() {
        return Bukkit.getPluginManager().isPluginEnabled("Vault");
    }

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Permission perms = null;

    public static boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = JavaPlugin.getPlugin(Main.class).getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static void GivePermission(Player player, String permission) {
        perms.playerAdd(player, permission);
    }

    public static void GivePermissionThatYouLeveledUpToLevel(Player player, int level) {
        GivePermission(player, "levels.level." + level);
    }

}
