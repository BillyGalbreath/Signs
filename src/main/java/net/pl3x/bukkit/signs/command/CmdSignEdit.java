package net.pl3x.bukkit.signs.command;

import net.pl3x.bukkit.signs.Util;
import net.pl3x.bukkit.signs.configuration.Lang;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdSignEdit implements TabExecutor {
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

        if (args.length < 1) {
            Lang.send(sender, Lang.NO_LINE_SPECIFIED);
            return true;
        }

        int line = Util.getLineFromArg(sender, args[0]);
        if ((line < 0) || (line > 3)) {
            Lang.send(sender, Lang.LINE_OUT_OF_RANGE);
            return true;
        }

        Sign sign = Util.getSignLookingAt(player);
        if (sign == null) {
            Lang.send(sender, Lang.NOT_LOOKING_AT_SIGN);
            return true;
        }

        String[] lines = sign.getLines();

        String newText = Util.join(args);

        boolean append = cmd.getLabel().equals("signappend");

        if (append) {
            newText = lines[line] + newText;
        }

        boolean prepend = cmd.getLabel().equals("signprepend");

        if (prepend) {
            newText = newText + lines[line];
        }

        lines[line] = newText;

        if (Util.editSign(player, sign, lines)) {
            if (append) {
                Lang.send(sender, Lang.SIGN_APPENDED);
            } else if (prepend) {
                Lang.send(sender, Lang.SIGN_PREPENDED);
            } else {
                Lang.send(sender, Lang.SIGN_EDITED);
            }
        }
        return true;
    }
}
