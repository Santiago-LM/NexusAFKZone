package nexus.nexusAFKZone;

import nexus.nexusAFKZone.commands.ZoneCreateCommand;
import nexus.nexusAFKZone.commands.DeleteCommand;
import nexus.nexusAFKZone.commands.ReloadCommand;
import nexus.nexusAFKZone.commands.ListCommand;
import nexus.nexusAFKZone.listeners.AFKListener;
import nexus.nexusAFKZone.listeners.SelectionListener;
import nexus.nexusAFKZone.managers.ZoneManager;
import nexus.nexusAFKZone.managers.RewardManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NexusAFKZone extends JavaPlugin {

    private ZoneManager zoneManager;
    private RewardManager rewardManager;
    private FileConfiguration messagesConfig;

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

        // Load messages config
        reloadMessagesConfig();

        // Register commands
        registerCommands();

        // Register listeners
        getServer().getPluginManager().registerEvents(new AFKListener(this), this);
        getServer().getPluginManager().registerEvents(new SelectionListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("NexusAFKZone disabled.");
    }

    private void registerCommands() {
        PluginCommand afkzoneCommand = getCommand("afkzone");
        if (afkzoneCommand != null) {
            ZoneCreateCommand createCommand = new ZoneCreateCommand(this);
            DeleteCommand deleteCommand = new DeleteCommand(this);
            ReloadCommand reloadCommand = new ReloadCommand(this);
            ListCommand listCommand = new ListCommand(this);

            afkzoneCommand.setExecutor((sender, command, label, args) -> {
                if (args.length > 0) {
                    return switch (args[0].toLowerCase()) {
                        case "create" -> createCommand.onCommand(sender, command, label, args);
                        case "delete" -> deleteCommand.onCommand(sender, command, label, args);
                        case "reload" -> reloadCommand.onCommand(sender, command, label, args);
                        case "list" -> listCommand.onCommand(sender, command, label, args);
                        default -> {
                            sender.sendMessage("Unknown subcommand. Use /afkzone <create|delete|reload|list>.");
                            yield false;
                        }
                    };
                } else {
                    sender.sendMessage("Usage: /afkzone <create|delete|reload|list>");
                    return false;
                }
            });
        } else {
            getLogger().severe("Failed to register commands. Please check your plugin.yml.");
        }
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void reloadMessagesConfig() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
}