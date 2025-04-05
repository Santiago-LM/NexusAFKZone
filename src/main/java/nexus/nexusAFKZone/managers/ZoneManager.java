package nexus.nexusAFKZone.managers;

import nexus.nexusAFKZone.NexusAFKZone;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
            File[] files = zoneFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".yml")) {
                        zones.add(file.getName().replace(".yml", ""));
                    }
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

        String pos1WorldName = positions[0].getWorld() != null ? positions[0].getWorld().getName() : null;
        String pos2WorldName = positions[1].getWorld() != null ? positions[1].getWorld().getName() : null;

        if (pos1WorldName == null || pos2WorldName == null) {
            player.sendMessage("Error: One or both positions have an invalid world.");
            return;
        }

        config.set("pos1.world", pos1WorldName);
        config.set("pos1.x", positions[0].getX());
        config.set("pos1.y", positions[0].getY());
        config.set("pos1.z", positions[0].getZ());
        config.set("pos2.world", pos2WorldName);
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
            plugin.getLogger().severe("An error occurred while creating the zone: " + e.getMessage());
        }
    }

    public Location[] getZoneBounds(String zoneName) {
        File zoneFile = new File(plugin.getDataFolder() + "/Zones", zoneName + ".yml");
        if (!zoneFile.exists()) {
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(zoneFile);
        String worldName1 = config.getString("pos1.world");
        String worldName2 = config.getString("pos2.world");
        if (worldName1 == null || worldName2 == null) {
            return null;
        }

        Location pos1 = new Location(
                plugin.getServer().getWorld(worldName1),
                config.getDouble("pos1.x"),
                config.getDouble("pos1.y"),
                config.getDouble("pos1.z")
        );
        Location pos2 = new Location(
                plugin.getServer().getWorld(worldName2),
                config.getDouble("pos2.x"),
                config.getDouble("pos2.y"),
                config.getDouble("pos2.z")
        );

        return new Location[]{pos1, pos2};
    }
}