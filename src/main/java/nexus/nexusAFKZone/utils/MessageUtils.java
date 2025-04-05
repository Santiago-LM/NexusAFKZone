package nexus.nexusAFKZone.utils;

import org.bukkit.ChatColor;

public class MessageUtils {

    public static String format(String message, Object... args) {
        return ChatColor.translateAlternateColorCodes('&', String.format(message, args));
    }
}