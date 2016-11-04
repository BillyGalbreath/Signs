package net.pl3x.bukkit.pl3xsigns.command;

import net.pl3x.bukkit.pl3xsigns.Logger;
import net.pl3x.bukkit.pl3xsigns.Pl3xSigns;
import net.pl3x.bukkit.pl3xsigns.configuration.Config;
import net.pl3x.bukkit.pl3xsigns.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

public class CmdPl3xSigns implements TabExecutor {
    private Pl3xSigns plugin;

    public CmdPl3xSigns(Pl3xSigns plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase())) {
            return Collections.singletonList("reload");
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.pl3xsigns")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Logger.debug("Reloading config...");
            Config.reload();

            Logger.debug("Reloading language file...");
            Lang.reload();

            Lang.send(sender, Lang.RELOAD
                    .replace("{plugin}", plugin.getName())
                    .replace("{version}", plugin.getDescription().getVersion()));
            return true;
        }

        Lang.send(sender, Lang.VERSION
                .replace("{version}", plugin.getDescription().getVersion())
                .replace("{plugin}", plugin.getName()));
        return true;
    }
}
