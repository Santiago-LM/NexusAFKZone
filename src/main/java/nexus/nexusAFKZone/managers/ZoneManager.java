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
    private final Map<UUID, String> pendingZoneNames; // Store pending zone names

    public ZoneManager(NexusAFKZone plugin) {
        this.plugin = plugin;
        this.selections = new HashMap<>();
        this.zones = new HashMap<>();
        this.pendingZoneNames = new HashMap<>(); // Initialize the map
        loadZones();
    }

    public boolean deleteZone(String zoneName) {
        zones.remove(zoneName);
        File zoneFile = new File(plugin.getDataFolder() + "/Zones", zoneName + ".yml");
        boolean result = zoneFile.delete();
        if (result) {
            plugin.getLogger().info("Zone " + zoneName + " deleted successfully.");
        } else {
            plugin.getLogger().warning("Failed to delete zone " + zoneName + ".");
        }
        return result;
    }

    public List<String> listZones() {
        return new ArrayList<>(zones.keySet());
    }

    public void setSelection(Player player, Location pos1, Location pos2) {
        selections.put(player.getUniqueId(), new Location[]{pos1, pos2});
        plugin.getLogger().info("Selection set for player " + player.getName() + ": " + pos1 + ", " + pos2);
    }

    public Location[] getSelection(Player player) {
        return selections.get(player.getUniqueId());
    }

    public void setPendingZoneName(Player player, String zoneName) {
        pendingZoneNames.put(player.getUniqueId(), zoneName);
        plugin.getLogger().info("Pending zone name set for player " + player.getName() + ": " + zoneName);
    }

    public String getPendingZoneName(Player player) {
        return pendingZoneNames.get(player.getUniqueId());
    }

    public void clearPendingZoneName(Player player) {
        pendingZoneNames.remove(player.getUniqueId());
        plugin.getLogger().info("Pending zone name cleared for player " + player.getName());
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

        saveZone(zoneName, positions[0], positions[1]); // Call saveZone to save the zone

        zones.put(zoneName, positions);
        clearPendingZoneName(player); // Clear the pending zone name after creation
        player.sendMessage("Zone " + zoneName + " created successfully.");
        plugin.getLogger().info("Zone " + zoneName + " created by player " + player.getName());
    }

    public String confirmZone(Player player) {
        String zoneName = getPendingZoneName(player); // Retrieve the pending zone name
        if (zoneName == null) {
            player.sendMessage("No zone name found. Please use /afkzone create <name> first.");
            return null;
        }
        Location[] positions = selections.get(player.getUniqueId());
        if (positions == null || positions[0] == null || positions[1] == null) {
            player.sendMessage("You must select both positions first.");
            return null;
        }
        createZone(player, zoneName); // Call createZone to handle the zone creation
        clearPendingZoneName(player); // Clear the pending zone name after confirmation
        return zoneName;
    }

    public void clearSelection(Player player) {
        selections.remove(player.getUniqueId());
        plugin.getLogger().info("Selection cleared for player " + player.getName());
    }

    public Location[] getZoneBounds(String zoneName) {
        return zones.get(zoneName);
    }

    public void clearZones() {
        zones.clear();
        plugin.getLogger().info("All zones cleared.");
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