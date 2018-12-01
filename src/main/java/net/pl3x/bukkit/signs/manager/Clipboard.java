package net.pl3x.bukkit.signs.manager;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Clipboard {
    private static final HashMap<UUID, Clipboard> clipboards = new HashMap<>();

    public static Clipboard get(Player player) {
        return clipboards.get(player.getUniqueId());
    }

    public static void set(Player player, Clipboard clipboard) {
        clipboards.put(player.getUniqueId(), clipboard);
    }


    private final String[] lines = new String[4];

    public Clipboard(Sign sign) {
        System.arraycopy(sign.getLines(), 0, lines, 0, 4);
    }

    public String getLine(int i) {
        if (lines.length < i || lines[i] == null) {
            return "";
        }
        return lines[i];
    }

    public String[] getLines() {
        return lines;
    }
}
