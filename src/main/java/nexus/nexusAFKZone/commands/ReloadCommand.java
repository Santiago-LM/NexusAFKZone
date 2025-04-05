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
        if (sender instanceof Player player) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("nexusafkzone.command.reload")) {
                    plugin.reloadConfig();
                    plugin.reloadMessagesConfig();
                    player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("config-reloaded")));
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