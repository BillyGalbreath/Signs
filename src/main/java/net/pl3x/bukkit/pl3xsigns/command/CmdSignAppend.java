package net.pl3x.bukkit.pl3xsigns.command;

import net.pl3x.bukkit.pl3xsigns.Logger;
import net.pl3x.bukkit.pl3xsigns.Pl3xSigns;
import net.pl3x.bukkit.pl3xsigns.configuration.Lang;
import net.pl3x.bukkit.pl3xsigns.history.HistoryEntry;
import net.pl3x.bukkit.pl3xsigns.history.HistoryManager;
import net.pl3x.bukkit.pl3xsigns.lineofsight.LineOfSight;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class CmdSignAppend implements TabExecutor {
    private final Pl3xSigns plugin;

    public CmdSignAppend(Pl3xSigns plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("command.signappend")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }
        if (args.length < 1) {
            Lang.send(sender, Lang.NO_LINE_SPECIFIED);
            return true;
        }

        Block block = LineOfSight.getTargetBlock(player);
        if (block == null || !(block.getState() instanceof Sign)) {
            Lang.send(sender, Lang.NOT_LOOKING_AT_SIGN);
            return true;
        }
        Integer line;
        try {
            line = Integer.parseInt(args[0]);
            line--;
        } catch (NumberFormatException e) {
            Lang.send(sender, Lang.LINE_MUST_BE_NUMBER);
            return true;
        }
        if ((line < 0) || (line > 3)) {
            Lang.send(sender, Lang.LINE_OUT_OF_RANGE);
            return true;
        }

        Sign sign = (Sign) block.getState();
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = ChatColor.stripColor(sign.getLine(i).replace("\u00a7", "&"));
        }
        lines[line] += (args.length < 2) ? "" : CmdSignEdit.join(args);
        HistoryEntry history = new HistoryEntry(sign); // get current lines of sign

        // call SignChangeEvent (makes the command compatible with protection plugins)
        Logger.debug("[Append Command] Calling BlockBreakEvent.");
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
        Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
        if (blockBreakEvent.isCancelled()) {
            Lang.send(sender, Lang.CANNOT_PUT_SIGN_HERE);
            return true;
        }

        Logger.debug("[Append Command] Calling SignChangeEvent.");
        SignChangeEvent signChangeEvent = new SignChangeEvent(block, player, lines);
        Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
        if (signChangeEvent.isCancelled()) {
            Lang.send(sender, Lang.CANNOT_EDIT_SIGN_HERE);
            return true;
        }

        lines = signChangeEvent.getLines();
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, player.hasPermission("sign.color") ? ChatColor.translateAlternateColorCodes('&', lines[i]) : lines[i]);
        }
        sign.update();

        Logger.debug("[Append Command] Sign text appended.");
        HistoryManager.getHistory(player).add(history); // store original lines of sign
        Lang.send(sender, Lang.SIGN_APPENDED);
        return true;
    }
}
