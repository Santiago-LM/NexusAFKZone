package nexus.nexusAFKZone.commands;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final NexusAFKZone plugin;

    public ReloadCommand(NexusAFKZone plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Ensure the sender is a player
        if (sender instanceof Player player) {
            // Check if the command is "reload"
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                // Check if the player has the required permission
                if (player.hasPermission("nexusafkzone.command.reload")) {
                    // Reload the configuration files
                    try {
                        plugin.reloadConfig();
                        plugin.reloadMessagesConfig();
                        plugin.getZoneManager().loadZones();
                        plugin.getRewardManager().reloadRewards(); // Reload rewards in RewardManager

                        // Notify the player that the configuration has been reloaded
                        player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("config-reloaded")));
                    } catch (Exception e) {
                        // Log any errors that occur during reloading
                        plugin.getLogger().severe("An error occurred while reloading the configuration: " + e.getMessage());
                        player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("reload-failed")));
                    }
                } else {
                    // Notify the player that they do not have permission to use the command
                    player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("no-permission")));
                }
                return true;
            }
        } else {
            // Notify the sender that the command can only be used by players
            sender.sendMessage("This command can only be used by players.");
        }

        return false;
    }
}