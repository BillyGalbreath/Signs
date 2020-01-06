package net.pl3x.bukkit.signs;

import net.pl3x.bukkit.signs.command.CmdSignCopy;
import net.pl3x.bukkit.signs.command.CmdSignEdit;
import net.pl3x.bukkit.signs.command.CmdSigns;
import net.pl3x.bukkit.signs.configuration.Config;
import net.pl3x.bukkit.signs.configuration.Lang;
import net.pl3x.bukkit.signs.listener.SignListener;
import net.pl3x.bukkit.signs.protocollib.ProtocolLibHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Signs extends JavaPlugin {
    private static Signs instance;

    public Signs() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        getCommand("signs").setExecutor(new CmdSigns(this));

        getCommand("signedit").setExecutor(new CmdSignEdit());
        getCommand("signprepend").setExecutor(new CmdSignEdit());
        getCommand("signappend").setExecutor(new CmdSignEdit());

        getCommand("signcopy").setExecutor(new CmdSignCopy());
        getCommand("signpaste").setExecutor(new CmdSignCopy());
        getCommand("signundo").setExecutor(new CmdSignCopy());

        Bukkit.getPluginManager().registerEvents(new SignListener(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            ProtocolLibHook.registerPacketListeners(this);
        }
    }

    public static Signs getInstance() {
        return instance;
    }
}
