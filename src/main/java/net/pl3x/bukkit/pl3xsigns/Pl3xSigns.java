package net.pl3x.bukkit.pl3xsigns;

import net.pl3x.bukkit.pl3xsigns.command.CmdPl3xSigns;
import net.pl3x.bukkit.pl3xsigns.command.CmdSignAppend;
import net.pl3x.bukkit.pl3xsigns.command.CmdSignCopy;
import net.pl3x.bukkit.pl3xsigns.command.CmdSignEdit;
import net.pl3x.bukkit.pl3xsigns.command.CmdSignPaste;
import net.pl3x.bukkit.pl3xsigns.command.CmdSignUndo;
import net.pl3x.bukkit.pl3xsigns.configuration.Config;
import net.pl3x.bukkit.pl3xsigns.configuration.Lang;
import net.pl3x.bukkit.pl3xsigns.hook.ProtocolLibHook;
import net.pl3x.bukkit.pl3xsigns.listener.SignListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xSigns extends JavaPlugin {
    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolLibHook.registerPacketListeners(this);
        }

        Bukkit.getPluginManager().registerEvents(new SignListener(), this);

        getCommand("pl3xsigns").setExecutor(new CmdPl3xSigns(this));
        getCommand("signappend").setExecutor(new CmdSignAppend(this));
        getCommand("signcopy").setExecutor(new CmdSignCopy(this));
        getCommand("signedit").setExecutor(new CmdSignEdit(this));
        getCommand("signpaste").setExecutor(new CmdSignPaste(this));
        getCommand("signundo").setExecutor(new CmdSignUndo(this));

        Logger.info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        Logger.info(getName() + " disabled.");
    }

    public static Pl3xSigns getPlugin() {
        return Pl3xSigns.getPlugin(Pl3xSigns.class);
    }
}
