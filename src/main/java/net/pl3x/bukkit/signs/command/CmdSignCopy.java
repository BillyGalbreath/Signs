package net.pl3x.bukkit.signs.command;

import net.pl3x.bukkit.signs.Util;
import net.pl3x.bukkit.signs.manager.Clipboard;
import net.pl3x.bukkit.signs.configuration.Lang;
import net.pl3x.bukkit.signs.manager.History;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdSignCopy implements TabExecutor {
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
        if (!player.hasPermission("command." + cmd.getLabel())) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Sign sign = Util.getSignLookingAt(player);
        if (sign == null) {
            Lang.send(sender, Lang.NOT_LOOKING_AT_SIGN);
            return true;
        }

        if (cmd.getLabel().equals("signcopy")) {
            Clipboard.set(player, new Clipboard(sign));
            Lang.send(sender, Lang.SIGN_COPIED);
            return true;
        }

        if (cmd.getLabel().equals("signundo")) {
            String[] lines = History.get(sign).remove();
            if (lines == null) {
                Lang.send(sender, Lang.NOTHING_TO_UNDO);
                return true;
            }

            if (Util.editSign(player, sign, lines)) {
                Lang.send(sender, Lang.SIGN_UNDONE);
            }
            return true;
        }

        Clipboard clipboard = Clipboard.get(player);
        if (clipboard == null) {
            Lang.send(sender, Lang.CLIPBOARD_EMPTY);
            return true;
        }

        String[] lines = clipboard.getLines();
        if (lines == null || lines.length == 0) {
            Lang.send(sender, Lang.CLIPBOARD_EMPTY);
            return true;
        }

        if (Util.editSign(player, sign, lines)) {
            Lang.send(sender, Lang.SIGN_PASTED);
        }
        return true;
    }
}
