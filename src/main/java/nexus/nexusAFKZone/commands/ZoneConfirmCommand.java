package nexus.nexusAFKZone.commands;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.managers.ZoneManager;
import nexus.nexusAFKZone.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
            Location[] selection = zoneManager.getSelection(player);
            if (selection != null && selection[0] != null && selection[1] != null) {
                String zoneName = args.length > 0 ? args[0] : "default-zone";
                zoneName = zoneManager.confirmZone(zoneName, selection[0], selection[1]);
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-create-success"), zoneName));
                zoneManager.clearSelection(player);
                removeWandFromInventory(player);
            } else {
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-create-failure")));
            }
            return true;
        } else {
            sender.sendMessage("This command can only be used by players.");
        }

        return false;
    }

    private void removeWandFromInventory(Player player) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD); // Assuming the wand is a blaze rod
        player.getInventory().remove(wand);
    }
}