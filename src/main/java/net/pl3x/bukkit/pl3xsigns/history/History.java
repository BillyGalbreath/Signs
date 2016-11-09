package net.pl3x.bukkit.pl3xsigns.history;

import net.pl3x.bukkit.pl3xsigns.configuration.Config;

import java.util.ArrayList;
import java.util.List;

public class History {
    private static final List<HistoryEntry> entries = new ArrayList<>();

    public HistoryEntry get() {
        if (entries.isEmpty()) {
            return null;
        }
        return entries.get(0);
    }

    public void add(HistoryEntry entry) {
        entries.add(0, entry);

        int max = Config.MAX_UNDO;
        if (entries.size() > max) {
            entries.subList(max, entries.size()).clear();
        }
    }

    public void remove() {
        if (entries.isEmpty()) {
            return;
        }
        entries.remove(0);
    }
}
