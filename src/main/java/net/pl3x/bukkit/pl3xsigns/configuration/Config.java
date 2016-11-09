package net.pl3x.bukkit.pl3xsigns.configuration;

import net.pl3x.bukkit.pl3xsigns.Pl3xSigns;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public static boolean COLOR_LOGS = true;
    public static boolean DEBUG_MODE = false;
    public static String LANGUAGE_FILE = "lang-en.yml";
    public static int MAX_UNDO = 10;
    public static boolean USE_OLD_LINE_OF_SIGHT = false;
    public static int LINE_OF_SIGHT_DISTANCE = 5;
    public static double LINE_OF_SIGHT_ACCURACY = 0.01;

    public static void reload() {
        Pl3xSigns plugin = Pl3xSigns.getPlugin();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        COLOR_LOGS = config.getBoolean("color-logs", true);
        DEBUG_MODE = config.getBoolean("debug-mode", false);
        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");
        MAX_UNDO = config.getInt("max-undo", 10);
        USE_OLD_LINE_OF_SIGHT = config.getBoolean("use-old-line-of-sight", false);
        LINE_OF_SIGHT_DISTANCE = config.getInt("line-of-sight-distance", 5);
        LINE_OF_SIGHT_ACCURACY = config.getDouble("line-of-sight-accuracy", 0.01);
    }
}
