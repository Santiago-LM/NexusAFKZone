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
    private final Map<String, Location[]> zones;

    public ZoneManager(NexusAFKZone plugin) {
        this.plugin = plugin;
        this.selections = new HashMap<>();
        this.zones = new HashMap<>();
        loadZones();
    }

    public boolean deleteZone(String zoneName) {
        zones.remove(zoneName);
        File zoneFile = new File(plugin.getDataFolder() + "/Zones", zoneName + ".yml");
        return zoneFile.delete();
    }

    public List<String> listZones() {
        return new ArrayList<>(zones.keySet());
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
            zones.put(zoneName, positions);
            player.sendMessage("Zone " + zoneName + " created successfully.");
        } catch (IOException e) {
            plugin.getLogger().severe("An error occurred while creating the zone: " + e.getMessage());
        }
    }

    public String confirmZone(String zoneName, Location pos1, Location pos2) {
        zones.put(zoneName, new Location[]{pos1, pos2});
        saveZone(zoneName, pos1, pos2);
        return zoneName;
    }

    public void clearSelection(Player player) {
        selections.remove(player.getUniqueId());
    }

    public Location[] getZoneBounds(String zoneName) {
        return zones.get(zoneName);
    }

    public void clearZones() {
        zones.clear();
    }

    public void loadZone(File zoneFile) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(zoneFile);
        String zoneName = config.getString("zone.name");
        if (zoneName == null) {
            plugin.getLogger().warning("Zone name not found in file: " + zoneFile.getName());
            return;
        }

        String worldName1 = config.getString("pos1.world");
        String worldName2 = config.getString("pos2.world");
        if (worldName1 == null || worldName2 == null) {
            plugin.getLogger().warning("Invalid world names in zone file: " + zoneFile.getName());
            return;
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

        zones.put(zoneName, new Location[]{pos1, pos2});
        plugin.getLogger().info("Loaded zone: " + zoneName);
    }

    public void loadZones() {
        clearZones();
        File zonesDir = new File(plugin.getDataFolder(), "Zones");
        if (!zonesDir.exists() || !zonesDir.isDirectory()) {
            plugin.getLogger().warning("Zones directory not found. Skipping zone reload.");
            return;
        }

        File[] zoneFiles = zonesDir.listFiles();
        if (zoneFiles == null) {
            plugin.getLogger().warning("No zone files found in zones directory.");
            return;
        }

        for (File zoneFile : zoneFiles) {
            if (zoneFile.isFile() && zoneFile.getName().endsWith(".yml")) {
                loadZone(zoneFile);
            }
        }

        plugin.getLogger().info("Zones reloaded successfully.");
    }

    private void saveZone(String zoneName, Location pos1, Location pos2) {
        File zoneFile = new File(plugin.getDataFolder() + "/Zones", zoneName + ".yml");

        YamlConfiguration config = new YamlConfiguration();
        config.set("zone.name", zoneName);
        config.set("enabled", true);
        config.set("permission", "nexusafkzone.zone." + zoneName);

        String pos1WorldName = pos1.getWorld() != null ? pos1.getWorld().getName() : null;
        String pos2WorldName = pos2.getWorld() != null ? pos2.getWorld().getName() : null;

        if (pos1WorldName == null || pos2WorldName == null) {
            plugin.getLogger().warning("Error: One or both positions have an invalid world.");
            return;
        }

        config.set("pos1.world", pos1WorldName);
        config.set("pos1.x", pos1.getX());
        config.set("pos1.y", pos1.getY());
        config.set("pos1.z", pos1.getZ());
        config.set("pos2.world", pos2WorldName);
        config.set("pos2.x", pos2.getX());
        config.set("pos2.y", pos2.getY());
        config.set("pos2.z", pos2.getZ());
        config.set("rewards", plugin.getConfig().getStringList("default-rewards"));
        config.set("interval", plugin.getConfig().getInt("auto-save-interval"));
        config.set("messages.bossbar", "&6AFK in %zone% | Next reward in: %time%");
        config.set("messages.actionbar", "&a+%reward% in %time%");
        config.set("messages.chat", "&eYou earned %reward%!");

        try {
            config.save(zoneFile);
            plugin.getLogger().info("Zone " + zoneName + " saved successfully.");
        } catch (IOException e) {
            plugin.getLogger().severe("An error occurred while saving the zone: " + e.getMessage());
        }
    }
}