package net.pl3x.bukkit.pl3xsigns.history;

import org.bukkit.Location;
import org.bukkit.block.Sign;

public class HistoryEntry {
    private final Location location;
    private String[] lines = new String[4];

    public HistoryEntry(Sign sign) {
        location = sign.getLocation();
        lines = sign.getLines().clone();
    }

    public Location getLocation() {
        return location;
    }

    public String[] getLines() {
        return lines;
    }
}
