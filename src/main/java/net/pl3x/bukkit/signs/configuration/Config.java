package net.pl3x.bukkit.signs.configuration;

import net.pl3x.bukkit.signs.Signs;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public static String LANGUAGE_FILE = "lang-en.yml";
    public static int MAX_UNDO = 10;

    public static void reload() {
        Signs plugin = Signs.getInstance();
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        LANGUAGE_FILE = config.getString("language-file", "lang-en.yml");
        MAX_UNDO = config.getInt("max-undo", 10);
    }
}
