package nexus.nexusAFKZone.managers;

import nexus.nexusAFKZone.NexusAFKZone;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardManager {

    private final NexusAFKZone plugin;
    private final Map<UUID, Long> playerAFKTime;

    public RewardManager(NexusAFKZone plugin) {
        this.plugin = plugin;
        playerAFKTime = new HashMap<>();
        startRewardTask();
    }

    public void giveReward(Player player, String rewardCommand) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand.replace("%player%", player.getName()));
    }

    public void setPlayerAFK(Player player) {
        playerAFKTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void removePlayerAFK(Player player) {
        playerAFKTime.remove(player.getUniqueId());
    }

    private void startRewardTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerUUID : playerAFKTime.keySet()) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player == null) continue;

                    long afkTime = (System.currentTimeMillis() - playerAFKTime.get(playerUUID)) / 1000;
                    if (afkTime >= plugin.getConfig().getInt("auto-save-interval")) {
                        for (String reward : plugin.getConfig().getStringList("default-rewards")) {
                            giveReward(player, reward);
                        }
                        player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("reward-message"), afkTime));
                        playerAFKTime.put(playerUUID, System.currentTimeMillis());
                    }
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }
}