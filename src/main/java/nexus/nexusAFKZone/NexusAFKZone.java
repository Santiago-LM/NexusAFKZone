package nexus.nexusAFKZone;

import nexus.nexusAFKZone.commands.ZoneCreateCommand;
import nexus.nexusAFKZone.commands.DeleteCommand;
import nexus.nexusAFKZone.commands.ReloadCommand;
import nexus.nexusAFKZone.commands.ListCommand;
import nexus.nexusAFKZone.listeners.AFKListener;
import nexus.nexusAFKZone.listeners.SelectionListener;
import nexus.nexusAFKZone.managers.ZoneManager;
import nexus.nexusAFKZone.managers.RewardManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NexusAFKZone extends JavaPlugin {

    private ZoneManager zoneManager;
    private RewardManager rewardManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("NexusAFKZone enabled.");

        // Save default config and messages if not exists
        saveDefaultConfig();
        saveResource("messages.yml", false);

        // Initialize managers
        zoneManager = new ZoneManager(this);
        rewardManager = new RewardManager(this);

        // Register commands
        this.getCommand("afkzone").setExecutor(new ZoneCreateCommand(this));
        this.getCommand("afkzone").setExecutor(new DeleteCommand(this));
        this.getCommand("afkzone").setExecutor(new ReloadCommand(this));
        this.getCommand("afkzone").setExecutor(new ListCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new AFKListener(this), this);
        getServer().getPluginManager().registerEvents(new SelectionListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("NexusAFKZone disabled.");
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }
}