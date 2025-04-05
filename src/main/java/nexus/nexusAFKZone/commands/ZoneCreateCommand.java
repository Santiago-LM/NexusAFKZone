package nexus.nexusAFKZone.commands;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ZoneCreateCommand implements CommandExecutor {

    private final NexusAFKZone plugin;

    public ZoneCreateCommand(NexusAFKZone plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
                if (player.hasPermission("nexusafkzone.command.create")) {
                    String zoneName = args[1];
                    plugin.getZoneManager().createZone(player, zoneName);
                } else {
                    player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("no-permission")));
                }
                return true;
            } else if (args.length == 1 && args[0].equalsIgnoreCase("wand")) {
                if (player.hasPermission("nexusafkzone.command.create")) {
                    ItemStack wand = new ItemStack(Material.valueOf(plugin.getConfig().getString("default-wand")));
                    player.getInventory().addItem(wand);
                    player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("wand-received")));
                } else {
                    player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("no-permission")));
                }
                return true;
            }
        } else {
            sender.sendMessage("This command can only be used by players.");
        }

        return false;
    }
}