package nexus.nexusAFKZone.listeners;

import nexus.nexusAFKZone.NexusAFKZone;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.Action;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionListener implements Listener {

    private final NexusAFKZone plugin;
    private final Map<UUID, Location[]> selections;

    public SelectionListener(NexusAFKZone plugin) {
        this.plugin = plugin;
        selections = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.valueOf(plugin.getConfig().getString("default-wand"))) {
            return;
        }

        UUID playerUUID = player.getUniqueId();
        Action action = event.getAction();
        Location location = event.getClickedBlock().getLocation();

        if (action == Action.LEFT_CLICK_BLOCK) {
            selections.put(playerUUID, new Location[]{location, selections.getOrDefault(playerUUID, new Location[2])[1]});
            player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("position-1-set")));
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            selections.put(playerUUID, new Location[]{selections.getOrDefault(playerUUID, new Location[2])[0], location});
            player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("position-2-set")));
        }
    }
}