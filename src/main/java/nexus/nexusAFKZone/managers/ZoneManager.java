package nexus.nexusAFKZone.managers;

import nexus.nexusAFKZone.NexusAFKZone;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ZoneManager {

    private final NexusAFKZone plugin;
    private final Map<UUID, Location[]> selections;

    public ZoneManager(NexusAFKZone plugin) {
        this.plugin = plugin;
        selections = new HashMap<>();
    }

    public boolean deleteZone(String zoneName) {
        File zoneFile = new File(plugin.getDataFolder() + "/Zones", zoneName + ".yml");
        return zoneFile.delete();
    }

    public List<String> listZones() {
        File zoneFolder = new File(plugin.getDataFolder() + "/Zones");
        List<String> zones = new ArrayList<>();

        if (zoneFolder.exists() && zoneFolder.isDirectory()) {
            for (File file : zoneFolder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    zones.add(file.getName().replace(".yml", ""));
                }
            }
        }

        return zones;
    }

    public void setSelection(Player player, Location pos1, Location pos2) {
        selections.put(player.getUniqueId(), new Location[]{pos1, pos2});
    }

    public Location[] getSelection(Player player) {
        return selections.get(player.getUniqueId());
    }

    public void createZone(Player player, String zoneName) {
        Location[] positions = selections.get(player.getUniqueId());
        if (positions == null || positions[0] == null || positions[1] == null) {
            player.sendMessage("You must select both positions first.");
            return;
        }

        File zoneFile = new File(plugin.getDataFolder() + "/Zones", zoneName + ".yml");
        if (zoneFile.exists()) {
            player.sendMessage("A zone with this name already exists.");
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        config.set("enabled", true);
        config.set("permission", "nexusafkzone.zone." + zoneName);
        config.set("pos1.world", positions[0].getWorld().getName());
        config.set("pos1.x", positions[0].getX());
        config.set("pos1.y", positions[0].getY());
        config.set("pos1.z", positions[0].getZ());
        config.set("pos2.world", positions[1].getWorld().getName());
        config.set("pos2.x", positions[1].getX());
        config.set("pos2.y", positions[1].getY());
        config.set("pos2.z", positions[1].getZ());
        config.set("rewards", plugin.getConfig().getStringList("default-rewards"));
        config.set("interval", plugin.getConfig().getInt("auto-save-interval"));
        config.set("messages.bossbar", "&6AFK in %zone% | Next reward in: %time%");
        config.set("messages.actionbar", "&a+%reward% in %time%");
        config.set("messages.chat", "&eYou earned %reward%!");

        try {
            config.save(zoneFile);
            player.sendMessage("Zone " + zoneName + " created successfully.");
        } catch (IOException e) {
            player.sendMessage("An error occurred while creating the zone.");
            e.printStackTrace();
        }
    }
}