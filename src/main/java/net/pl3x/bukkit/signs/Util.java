package net.pl3x.bukkit.signs;

import net.pl3x.bukkit.signs.configuration.Lang;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Util {
    public static int getLineFromArg(CommandSender sender, String arg) {
        try {
            return Integer.parseInt(arg) - 1;
        } catch (NumberFormatException e) {
            Lang.send(sender, Lang.LINE_MUST_BE_NUMBER);
            return -1;
        }
    }

    public static Sign getSignLookingAt(Player player) {
        Block block = player.getTargetBlock(5);
        if (block == null || !(block.getState() instanceof Sign)) {
            return null;
        }
        return (Sign) block.getState();
    }

    public static String join(String[] args) {
        return (args.length < 2) ? "" : Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
    }

    public static String[] fixLineColors(Player player, String[] lines) {
        boolean color = player.hasPermission("sign.color");
        for (int i = 0; i < 4; i++) {
            lines[i] = ChatColor.stripColor(lines[i].replace("\u00a7", "&"));
            lines[i] = color ? ChatColor.translateAlternateColorCodes('&', lines[i]) : lines[i];
        }
        return lines;
    }

    public static boolean editSign(Player player, Sign sign, String[] linesIn) {
        SignChangeEvent event = new SignChangeEvent(sign.getBlock(), player, fixLineColors(player, linesIn));
        if (!event.callEvent()) {
            Lang.send(player, Lang.CANNOT_EDIT_SIGN);
            return false;
        }
        String[] lines = event.getLines();
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, lines[i]);
        }
        sign.update();
        return true;
    }
}
