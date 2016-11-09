package net.pl3x.bukkit.pl3xsigns.configuration;

import net.pl3x.bukkit.pl3xsigns.Pl3xSigns;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static String COMMAND_NO_PERMISSION = "&4You do not have permission for that command!";
    public static String PLAYER_COMMAND = "&4This command is only available to players.";

    public static String CANNOT_PUT_SIGN_HERE = "&4Cannot place sign here!";
    public static String CANNOT_EDIT_SIGN_HERE = "&4Cannot edit sign here!";
    public static String NO_LINE_SPECIFIED = "&4You must specify a line number to append to!";
    public static String NOT_LOOKING_AT_SIGN = "&4Not looking at a sign!";
    public static String LINE_MUST_BE_NUMBER = "&4Line must be a number!";
    public static String LINE_OUT_OF_RANGE = "&4Acceptable line range is 1 - 4 only!";
    public static String CLIPBOARD_EMPTY = "&4Clipboard is empty! Use /signcopy first.";
    public static String NOTHING_TO_UNDO = "&4No edits to undo!";
    public static String UNDO_SIGN_MISSING = "&4Sign to undo is no longer there! Removed from undo history!";
    public static String SIGN_ALREADY_OPEN = "&4Someone is already editing this sign!";

    public static String SIGN_APPENDED = "&dSign appended.";
    public static String SIGN_COPIED = "&dSign copied. Use /signpaste to paste.";
    public static String SIGN_EDITED = "&dSign edited.";
    public static String SIGN_PASTED = "&dSign pasted.";
    public static String SIGN_UNDONE = "&dSing edit undone.";

    public static String VERSION = "&d{plugin} v{version}.";
    public static String RELOAD = "&d{plugin} v{version} reloaded.";

    public static void reload() {
        Pl3xSigns plugin = Pl3xSigns.getPlugin();
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        if (!configFile.exists()) {
            plugin.saveResource(Config.LANGUAGE_FILE, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&4You do not have permission for that command!");
        PLAYER_COMMAND = config.getString("player-command", "&4This command is only available to players.");

        CANNOT_PUT_SIGN_HERE = config.getString("cannot-put-sign-here", "&4Cannot place sign here!");
        CANNOT_EDIT_SIGN_HERE = config.getString("cannot-edit-sign-here", "&4Cannot edit sign here!");
        NO_LINE_SPECIFIED = config.getString("no-line-specified", "&4You must specify a line number to append to!");
        NOT_LOOKING_AT_SIGN = config.getString("not-looking-at-sign", "&4Not looking at a sign!");
        LINE_MUST_BE_NUMBER = config.getString("line-must-be-number", "&4Line must be a number!");
        LINE_OUT_OF_RANGE = config.getString("line-out-of-range", "&4Acceptable line range is 1 - 4 only!");
        CLIPBOARD_EMPTY = config.getString("clipboard-empty", "&4Clipboard is empty! Use /signcopy first.");
        NOTHING_TO_UNDO = config.getString("nothing-to-undo", "&4No edits to undo!");
        UNDO_SIGN_MISSING = config.getString("undo-sign-missing", "&4Sign to undo is no longer there! Removed from undo history!");
        SIGN_ALREADY_OPEN = config.getString("sign-already-open", "&4Someone is already editing this sign!");

        SIGN_APPENDED = config.getString("sign-appended", "&dSign appended.");
        SIGN_COPIED = config.getString("sign-copied", "&dSign copied. Use /signpaste to paste.");
        SIGN_EDITED = config.getString("sign-edited", "&dSign edited.");
        SIGN_PASTED = config.getString("sign-pasted", "&dSign pasted.");
        SIGN_UNDONE = config.getString("sign-undone", "&dSing edit undone.");

        VERSION = config.getString("version", "&d{plugin} v{version}.");
        RELOAD = config.getString("reload", "&d{plugin} v{version} reloaded.");
    }

    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }
}
