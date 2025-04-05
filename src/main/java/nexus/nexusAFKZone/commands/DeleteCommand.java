package nexus.nexusAFKZone.commands;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand implements CommandExecutor {

    private final NexusAFKZone plugin;

    public DeleteCommand(NexusAFKZone plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            if (player.hasPermission("nexusafkzone.command.delete")) {
                String zoneName = args[1];
                if (plugin.getZoneManager().deleteZone(zoneName)) {
                    player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-deleted"), zoneName));
                } else {
                    player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-not-found"), zoneName));
                }
            } else {
                player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("no-permission")));
            }
            return true;
        }

        return false;
    }
}