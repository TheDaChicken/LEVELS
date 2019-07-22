package level.plugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    private static List<ArmorStand> armorStandList = new ArrayList<ArmorStand>();

    public Hologram(Location location) {
        ArmorStand Main = createHologram(location);
        armorStandList.add(Main);
    }

    public ArmorStand createHologram(Location location) {
        // This is to use any armor stands that were not removed during a restart.
        Entity entity = location.getWorld().getNearbyEntities(location, 0, 0, 0).stream().filter(entity_1 -> entity_1.getType() == EntityType.ARMOR_STAND).findAny().orElse(null);
        ArmorStand am;
        if (entity != null) {
            am = (ArmorStand) entity;
        } else {
            am = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        }
        am.setArms(false);
        am.setGravity(false);
        am.setVisible(false);
        am.setCustomNameVisible(true);
        return am;
    }

    public void ChangeLine(int line, String text) {
        int lastof = armorStandList.size() - 1;
        if(lastof < line) {
            World world = armorStandList.get(lastof).getLocation().getWorld();
            double x = armorStandList.get(lastof).getLocation().getX();
            double y = armorStandList.get(lastof).getLocation().getY();
            double z = armorStandList.get(lastof).getLocation().getZ();
            Location location = new Location(world, x, y - 0.25, z);
            ArmorStand newline = createHologram(location);
            if (text.equals("")) {
                newline.setCustomNameVisible(false);
            }
            armorStandList.add(newline);
        }
        armorStandList.get(line).setCustomName(text);
    }

    public void Remove() {
        for(ArmorStand armorStand : armorStandList) {
            armorStand.remove();
        }
        armorStandList.clear();
    }
}
