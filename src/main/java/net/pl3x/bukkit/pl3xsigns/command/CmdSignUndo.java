package net.pl3x.bukkit.pl3xsigns.command;

import net.pl3x.bukkit.pl3xsigns.Logger;
import net.pl3x.bukkit.pl3xsigns.Pl3xSigns;
import net.pl3x.bukkit.pl3xsigns.configuration.Lang;
import net.pl3x.bukkit.pl3xsigns.history.History;
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

public class CmdSignUndo implements TabExecutor {
    private final Pl3xSigns plugin;

    public CmdSignUndo(Pl3xSigns plugin) {
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
        if (!player.hasPermission("command.signundo")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        History history = HistoryManager.getHistory(player);
        HistoryEntry lastEntry = history.get();
        if (lastEntry == null) {
            Lang.send(sender, Lang.NOTHING_TO_UNDO);
            return true;
        }

        history.remove(); // remove from history, even if errors or not

        Block block = LineOfSight.getTargetBlock(player);
        if (block == null || !(block.getState() instanceof Sign)) {
            Lang.send(sender, Lang.UNDO_SIGN_MISSING);
            return true;
        }

        Logger.debug("[Undo Command] Calling BlockBreakEvent.");
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
        Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
        if (blockBreakEvent.isCancelled()) {
            Lang.send(sender, Lang.CANNOT_PUT_SIGN_HERE);
            return true;
        }

        Logger.debug("[Undo Command] Calling SignChangeEvent.");
        SignChangeEvent signChangeEvent = new SignChangeEvent(block, player, lastEntry.getLines());
        Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
        if (signChangeEvent.isCancelled()) {
            Lang.send(sender, Lang.CANNOT_EDIT_SIGN_HERE);
            return true;
        }

        Sign sign = (Sign) block.getState();
        String[] lines = signChangeEvent.getLines();
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, player.hasPermission("sign.color") ? ChatColor.translateAlternateColorCodes('&', lines[i]) : lines[i]);
        }
        sign.update();

        Logger.debug("[Undo Command] Player has undone a sign edit.");
        Lang.send(sender, Lang.SIGN_UNDONE);
        return true;
    }
}
