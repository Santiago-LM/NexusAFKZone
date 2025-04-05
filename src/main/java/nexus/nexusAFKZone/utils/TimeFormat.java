package nexus.nexusAFKZone.utils;

public class TimeFormat {

    public static String formatTime(long time) {
        // Implement time formatting logic
        int seconds = (int) time % 60;
        int minutes = (int) (time / 60) % 60;
        int hours = (int) time / 3600;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}