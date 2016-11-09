package net.pl3x.bukkit.pl3xsigns.command;

import net.pl3x.bukkit.pl3xsigns.Logger;
import net.pl3x.bukkit.pl3xsigns.Pl3xSigns;
import net.pl3x.bukkit.pl3xsigns.clipboard.Clipboard;
import net.pl3x.bukkit.pl3xsigns.clipboard.ClipboardManager;
import net.pl3x.bukkit.pl3xsigns.configuration.Lang;
import net.pl3x.bukkit.pl3xsigns.lineofsight.LineOfSight;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdSignCopy implements TabExecutor {
    private final Pl3xSigns plugin;

    public CmdSignCopy(Pl3xSigns plugin) {
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
        if (!player.hasPermission("command.signcopy")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Block block = LineOfSight.getTargetBlock(player);
        if (block == null || !(block.getState() instanceof Sign)) {
            Lang.send(sender, Lang.NOT_LOOKING_AT_SIGN);
            return true;
        }

        Sign sign = (Sign) block.getState();
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = ChatColor.stripColor(sign.getLine(i).replace("\u00a7", "&"));
        }
        ClipboardManager.setClipboard(player, new Clipboard(lines));

        Logger.debug("[Copy Command] Player copied sign to clipboard.");
        Lang.send(sender, Lang.SIGN_COPIED);
        return true;
    }
}
