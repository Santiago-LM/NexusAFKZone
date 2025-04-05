package nexus.nexusAFKZone.commands;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
                    giveDefaultWand(player, zoneName);
                    player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("zone-create-initiate"), zoneName));
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

    private void giveDefaultWand(Player player, String zoneName) {
        String wandMaterialName = plugin.getConfig().getString("default-wand.material", "BLAZE_ROD");
        Material wandMaterial = Material.getMaterial(wandMaterialName.toUpperCase());
        if (wandMaterial == null) {
            wandMaterial = Material.BLAZE_ROD;
        }

        ItemStack wand = new ItemStack(wandMaterial);
        ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
            String displayName = MessageUtils.format(plugin.getConfig().getString("default-wand.display-name", "&6AFK Zone Selector"));
            meta.setDisplayName(displayName);
            List<String> lore = plugin.getConfig().getStringList("default-wand.lore");
            lore.replaceAll(line -> MessageUtils.format(line.replace("%creation_zone_name%", zoneName)));
            meta.setLore(lore);
            wand.setItemMeta(meta);
        }

        player.getInventory().addItem(wand);
        player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("wand-received")));
    }
}