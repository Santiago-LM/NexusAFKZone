package nexus.nexusAFKZone.managers;

import nexus.nexusAFKZone.NexusAFKZone;
import nexus.nexusAFKZone.utils.MessageUtils;
import nexus.nexusAFKZone.utils.TimeFormat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardManager {

    private final NexusAFKZone plugin;
    private final Map<UUID, Long> playerAFKTime;
    private final Map<UUID, String> playerZones; // Store the zone each player is in
    private BukkitRunnable rewardTask;

    public RewardManager(NexusAFKZone plugin) {
        this.plugin = plugin;
        this.playerAFKTime = new HashMap<>();
        this.playerZones = new HashMap<>();
        startRewardTask();
    }

    public void giveReward(Player player, String rewardCommand) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand.replace("%player%", player.getName()));
        plugin.getLogger().info("Reward given to player " + player.getName() + " with command: " + rewardCommand);
    }

    public void setPlayerAFK(Player player, String zoneName) {
        playerAFKTime.put(player.getUniqueId(), System.currentTimeMillis());
        playerZones.put(player.getUniqueId(), zoneName);
        plugin.getLogger().info("Player " + player.getName() + " set as AFK in zone " + zoneName);
    }

    public void removePlayerAFK(Player player) {
        playerAFKTime.remove(player.getUniqueId());
        playerZones.remove(player.getUniqueId());
        plugin.getLogger().info("Player " + player.getName() + " removed from AFK status");
    }

    public void reloadRewards() {
        if (rewardTask != null) {
            rewardTask.cancel();
        }
        plugin.getLogger().info("Reloading rewards...");
        startRewardTask();
    }

    private void startRewardTask() {
        rewardTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerUUID : playerAFKTime.keySet()) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player == null) continue;

                    long afkTime = (System.currentTimeMillis() - playerAFKTime.get(playerUUID)) / 1000;
                    String zoneName = playerZones.get(playerUUID);
                    if (zoneName == null) continue;

                    ZoneManager zoneManager = plugin.getZoneManager();
                    Location[] zoneBounds = zoneManager.getZoneBounds(zoneName);
                    if (zoneBounds == null) continue;

                    int zoneInterval = plugin.getConfig().getInt("auto-save-interval"); // Default to global interval
                    if (plugin.getConfig().contains("Zones." + zoneName + ".interval")) {
                        zoneInterval = plugin.getConfig().getInt("Zones." + zoneName + ".interval");
                    }

                    if (afkTime >= zoneInterval) {
                        for (String reward : plugin.getConfig().getStringList("Zones." + zoneName + ".rewards")) {
                            giveReward(player, reward);
                        }
                        String formattedTime = TimeFormat.formatTime(afkTime);
                        player.sendMessage(MessageUtils.format(plugin.getMessagesConfig().getString("reward-message"), formattedTime));
                        playerAFKTime.put(playerUUID, System.currentTimeMillis());
                        plugin.getLogger().info("Reward given to player " + player.getName() + " in zone " + zoneName + " after being AFK for " + formattedTime);
                    }
                }
            }
        };
        rewardTask.runTaskTimer(plugin, 20, 20);
    }
}