package nexus.nexusAFKZone.listeners;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.managers.ZoneManager;
import nexus.nexusAFKZone.utils.MessageUtils;
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

        if (item.getType() == getDefaultWandMaterial()) {
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
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("position-1-set")));
            } else if (event.getAction().toString().contains("RIGHT_CLICK")) {
                selection[1] = clickedLocation;
                zoneManager.setSelection(player, selection[0], selection[1]);
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("position-2-set")));
            }

            if (selection[0] != null && selection[1] != null) {
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-confirmation"), formatLocation(selection[0]), formatLocation(selection[1])));
                player.sendMessage("Type /afkzone confirm to finalize the AFK zone creation.");
            }

            event.setCancelled(true);
        }
    }

    private Material getDefaultWandMaterial() {
        String materialName = plugin.getConfig().getString("default-wand.material", "BLAZE_ROD");
        Material material = Material.getMaterial(materialName.toUpperCase());
        if (material == null) {
            plugin.getLogger().warning("Invalid default wand material in config.yml: " + materialName);
            material = Material.BLAZE_ROD;
        }
        return material;
    }

    private String formatLocation(Location loc) {
        return "X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z: " + loc.getBlockZ();
    }
}