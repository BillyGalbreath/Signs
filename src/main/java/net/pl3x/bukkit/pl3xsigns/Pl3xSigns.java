package net.pl3x.bukkit.pl3xsigns;

import net.pl3x.bukkit.pl3xsigns.command.CmdPl3xSigns;
import net.pl3x.bukkit.pl3xsigns.configuration.Config;
import net.pl3x.bukkit.pl3xsigns.configuration.Lang;
import net.pl3x.bukkit.pl3xsigns.listener.SignListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xSigns extends JavaPlugin {
    @Override
    public void onEnable() {
        Config.reload();
        Lang.reload();

        Bukkit.getPluginManager().registerEvents(new SignListener(), this);

        getCommand("pl3xsigns").setExecutor(new CmdPl3xSigns(this));

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
