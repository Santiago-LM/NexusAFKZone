package nexus.nexusAFKZone.commands;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.managers.ZoneManager;
import nexus.nexusAFKZone.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ZoneConfirmCommand implements CommandExecutor {

    private final NexusAFKZone plugin;
    private final ZoneManager zoneManager;

    public ZoneConfirmCommand(NexusAFKZone plugin) {
        this.plugin = plugin;
        this.zoneManager = plugin.getZoneManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            String zoneName = zoneManager.confirmZone(player); // Ensure confirmZone is called
            if (zoneName == null) {
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-create-failure")));
                return true;
            }
            player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-create-success"), zoneName));
            zoneManager.clearSelection(player);
            removeWandFromInventory(player);
            return true;
        } else {
            sender.sendMessage("This command can only be used by players.");
        }

        return false;
    }

    private void removeWandFromInventory(Player player) {
        player.getInventory().remove(Material.BLAZE_ROD); // Assuming the wand is a blaze rod
    }
}