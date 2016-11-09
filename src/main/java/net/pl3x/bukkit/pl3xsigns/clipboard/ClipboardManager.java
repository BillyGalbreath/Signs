package net.pl3x.bukkit.pl3xsigns.clipboard;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ClipboardManager {
    private static final HashMap<UUID, Clipboard> clipboards = new HashMap<>();

    public static Clipboard getClipboard(Player player) {
        return getClipboard(player.getUniqueId());
    }

    public static Clipboard getClipboard(UUID uuid) {
        return clipboards.get(uuid);
    }

    public static void setClipboard(Player player, Clipboard clipboard) {
        setClipboard(player.getUniqueId(), clipboard);
    }

    public static void setClipboard(UUID uuid, Clipboard clipboard) {
        clipboards.put(uuid, clipboard);
    }
}
