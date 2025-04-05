package nexus.nexusAFKZone.commands;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ZoneCreateCommand implements CommandExecutor {

    private final NexusAFKZone plugin;

    public ZoneCreateCommand(NexusAFKZone plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
            if (player.hasPermission("nexusafkzone.command.create")) {
                ItemStack wand = new ItemStack(Material.valueOf(plugin.getConfig().getString("default-wand")));
                player.getInventory().addItem(wand);
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("wand-received")));
                // Handle the rest of the zone creation process
            } else {
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("no-permission")));
            }
            return true;
        }

        return false;
    }
}