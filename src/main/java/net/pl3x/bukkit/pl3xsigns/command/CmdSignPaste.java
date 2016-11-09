package net.pl3x.bukkit.pl3xsigns.command;

import net.pl3x.bukkit.pl3xsigns.Logger;
import net.pl3x.bukkit.pl3xsigns.Pl3xSigns;
import net.pl3x.bukkit.pl3xsigns.clipboard.Clipboard;
import net.pl3x.bukkit.pl3xsigns.clipboard.ClipboardManager;
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

public class CmdSignPaste implements TabExecutor {
    private final Pl3xSigns plugin;

    public CmdSignPaste(Pl3xSigns plugin) {
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
        if (!player.hasPermission("command.signpaste")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Block block = LineOfSight.getTargetBlock(player);
        if (block == null || !(block.getState() instanceof Sign)) {
            Lang.send(sender, Lang.NOT_LOOKING_AT_SIGN);
            return true;
        }

        Clipboard clipboard = ClipboardManager.getClipboard(player);
        if (clipboard == null) {
            Lang.send(sender, Lang.CLIPBOARD_EMPTY);
            return true;
        }

        String[] lines = clipboard.getLines();
        if (lines == null || lines.length == 0) {
            Lang.send(sender, Lang.CLIPBOARD_EMPTY);
            return true;
        }
        HistoryEntry history = new HistoryEntry((Sign) block.getState()); // get current lines of sign

        Logger.debug("[Paste Command] Calling BlockBreakEvent.");
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
        Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
        if (blockBreakEvent.isCancelled()) {
            Lang.send(sender, Lang.CANNOT_PUT_SIGN_HERE);
            return true;
        }

        Logger.debug("[Paste Command] Calling SignChangeEvent.");
        SignChangeEvent signChangeEvent = new SignChangeEvent(block, player, lines);
        Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
        if (signChangeEvent.isCancelled()) {
            Lang.send(sender, Lang.CANNOT_EDIT_SIGN_HERE);
            return true;
        }

        lines = signChangeEvent.getLines();
        Sign sign = (Sign) block.getState();
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, player.hasPermission("sign.color") ? ChatColor.translateAlternateColorCodes('&', lines[i]) : lines[i]);
        }
        sign.update();

        Logger.debug("[Paste Command] Sign text pasted.");
        HistoryManager.getHistory(player).add(history); // store original lines of sign
        Lang.send(sender, Lang.SIGN_PASTED);
        return true;
    }
}
