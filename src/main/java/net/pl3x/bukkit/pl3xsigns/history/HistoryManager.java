package net.pl3x.bukkit.pl3xsigns.history;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class HistoryManager {
    private static final HashMap<UUID, History> histories = new HashMap<>();

    public static History getHistory(Player player) {
        return getHistory(player.getUniqueId());
    }

    public static History getHistory(UUID uuid) {
        if (!histories.containsKey(uuid)) {
            histories.put(uuid, new History());
        }
        return histories.get(uuid);
    }
}
