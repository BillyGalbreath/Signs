package net.pl3x.bukkit.signs.manager;

import net.pl3x.bukkit.signs.configuration.Config;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class History {
    private static final HashMap<Location, History> histories = new HashMap<>();

    public static History get(Sign sign) {
        Location loc = sign.getLocation();
        if (!histories.containsKey(loc)) {
            histories.put(loc, new History());
        }
        return histories.get(loc);
    }


    private final List<String[]> history = new ArrayList<>();

    public String[] get() {
        return history.get(0);
    }

    public void add(String[] entry) {
        history.add(0, entry);
        if (history.size() > Config.MAX_UNDO) {
            history.subList(Config.MAX_UNDO, history.size()).clear();
        }
    }

    public String[] remove() {
        return history.remove(0);
    }
}
