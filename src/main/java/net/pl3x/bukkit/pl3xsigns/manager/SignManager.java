package net.pl3x.bukkit.pl3xsigns.manager;

import org.bukkit.block.Sign;

import java.util.HashSet;
import java.util.Set;

public class SignManager {
    private static final Set<Sign> signs = new HashSet<>();

    public static boolean contains(Sign sign) {
        return signs.contains(sign);
    }

    public static void add(Sign sign) {
        signs.add(sign);
    }

    public static boolean remove(Sign sign) {
        return signs.remove(sign);
    }

    public static void clear() {
        signs.clear();
    }
}
