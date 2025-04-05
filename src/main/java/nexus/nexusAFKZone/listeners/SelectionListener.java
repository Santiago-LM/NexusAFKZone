package nexus.nexusAFKZone.listeners;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.managers.ZoneManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SelectionListener implements Listener {

    private final NexusAFKZone plugin;
    private final ZoneManager zoneManager;

    public SelectionListener(NexusAFKZone plugin) {
        this.plugin = plugin;
        this.zoneManager = plugin.getZoneManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.valueOf(plugin.getConfig().getString("default-wand"))) {
            if (event.getClickedBlock() == null) {
                return;
            }
            Location clickedLocation = event.getClickedBlock().getLocation();
            Location[] selection = zoneManager.getSelection(player);
            if (selection == null) {
                selection = new Location[2];
            }

            if (event.getAction().toString().contains("LEFT_CLICK")) {
                selection[0] = clickedLocation;
                zoneManager.setSelection(player, selection[0], selection[1]);
                player.sendMessage("First position set to: " + formatLocation(clickedLocation));
            } else if (event.getAction().toString().contains("RIGHT_CLICK")) {
                selection[1] = clickedLocation;
                zoneManager.setSelection(player, selection[0], selection[1]);
                player.sendMessage("Second position set to: " + formatLocation(clickedLocation));
            }
            event.setCancelled(true);
        }
    }

    private String formatLocation(Location loc) {
        return "X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z: " + loc.getBlockZ();
    }
}