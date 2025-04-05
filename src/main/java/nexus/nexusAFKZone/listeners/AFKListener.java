package nexus.nexusAFKZone.listeners;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.managers.ZoneManager;
import nexus.nexusAFKZone.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;

public class AFKListener implements Listener {

    private final NexusAFKZone plugin;
    private final ZoneManager zoneManager;

    public AFKListener(NexusAFKZone plugin) {
        this.plugin = plugin;
        this.zoneManager = plugin.getZoneManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();

        if (to == null) return;

        // Check if player moved to a new block
        if (to.getBlockX() == from.getBlockX() &&
                to.getBlockY() == from.getBlockY() &&
                to.getBlockZ() == from.getBlockZ()) {
            return;
        }

        zoneManager.listZones().forEach(zoneName -> {
            Location[] zoneBounds = zoneManager.getZoneBounds(zoneName);
            if (zoneBounds == null || zoneBounds.length < 2) return;

            Location pos1 = zoneBounds[0];
            Location pos2 = zoneBounds[1];

            if (isInsideZone(to, pos1, pos2) && !isInsideZone(from, pos1, pos2)) {
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-entered"), zoneName));
                plugin.getRewardManager().setPlayerAFK(player);
            } else if (!isInsideZone(to, pos1, pos2) && isInsideZone(from, pos1, pos2)) {
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-exited"), zoneName));
                plugin.getRewardManager().removePlayerAFK(player);
            }
        });
    }

    private boolean isInsideZone(Location loc, Location pos1, Location pos2) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
}