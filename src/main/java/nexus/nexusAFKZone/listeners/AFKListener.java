package nexus.nexusAFKZone.listeners;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.managers.ZoneManager;
import nexus.nexusAFKZone.utils.MessageUtils;
import nexus.nexusAFKZone.utils.ZoneUtils;
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

            if (ZoneUtils.isInsideZone(to, pos1, pos2) && !ZoneUtils.isInsideZone(from, pos1, pos2)) {
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-entered"), zoneName));
                plugin.getRewardManager().setPlayerAFK(player, zoneName);
            } else if (!ZoneUtils.isInsideZone(to, pos1, pos2) && ZoneUtils.isInsideZone(from, pos1, pos2)) {
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-exited"), zoneName));
                plugin.getRewardManager().removePlayerAFK(player);
            }
        });
    }
}