package net.pl3x.bukkit.pl3xsigns.listener;

import net.pl3x.bukkit.pl3xsigns.Logger;
import net.pl3x.bukkit.pl3xsigns.SignData;
import net.pl3x.bukkit.pl3xsigns.configuration.Lang;
import net.pl3x.bukkit.pl3xsigns.event.LoadSignPacketEvent;
import net.pl3x.bukkit.pl3xsigns.event.UpdateSignPacketEvent;
import net.pl3x.bukkit.pl3xsigns.history.HistoryEntry;
import net.pl3x.bukkit.pl3xsigns.history.HistoryManager;
import net.pl3x.bukkit.pl3xsigns.hook.NmsHook;
import net.pl3x.bukkit.pl3xsigns.manager.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SignListener implements Listener {
    /*
     * Handle coloring/styling of signs
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onColorizeSign(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("sign.color")) {
            return; // no color permission
        }

        Logger.debug("[SignChangeEvent] Player " + player.getName() + " has sign.color permission. Coloring text.");
        for (int i = 0; i < 4; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignEditorClose(SignChangeEvent event) {
        Logger.debug("[SignChangeEvent] Event triggered.");

        if (SignManager.remove((Sign) event.getBlock().getState())) {
            Logger.debug("[SignChangeEvent] Removed sign editor.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void saveSignEditHistory(SignChangeEvent event) {
        Logger.debug("[SignChangeEvent] Saving previous sign in undo history.");
        HistoryEntry history = new HistoryEntry((Sign) event.getBlock().getState());
        HistoryManager.getHistory(event.getPlayer()).add(history);
    }

    /*
     * Open sign editor window if sign was placed on another sign
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void signPlace(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return; // did not right click a block
        }

        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return; // is sneaking. ignore.
        }
        if (!player.hasPermission("click.signedit")) {
            return; // no permission
        }

        if (player.getInventory().getItemInMainHand().getType() != Material.SIGN) {
            return; // not holding a sign in main hand
        }

        Block block = event.getClickedBlock();
        if (!(block.getState() instanceof Sign)) {
            return; // sign wasn't clicked
        }

        Sign sign = (Sign) block.getState();
        if (SignManager.contains(sign)) {
            Lang.send(player, Lang.SIGN_ALREADY_OPEN);
            event.setCancelled(true);
            return; // sign is already being edited by another user
        }

        Logger.debug("[PlayerInteractEvent] Calling BlockBreakEvent.");
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
        Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
        if (blockBreakEvent.isCancelled()) {
            Lang.send(player, Lang.CANNOT_EDIT_SIGN_HERE);
            event.setCancelled(true);
            return; // cannot edit this sign
        }

        Logger.debug("[PlayerInteractEvent]" + player.getName() + " opened sign editor.");
        NmsHook.sendSignWindowPacket(player, sign);

        SignManager.add(sign);
        event.setCancelled(true);
    }

    @EventHandler
    public void onSignUpdatePacket(UpdateSignPacketEvent event) {
        String[] lines = event.getLines();
        for (int i = 0; i < 4; i++) {
            lines[i] = lines[i]
                    .replace("{player}", event.getPlayer().getName());
        }
        event.setLines(lines);
    }

    @EventHandler
    public void onSignLoadPacket(LoadSignPacketEvent event) {
        List<SignData> signDatas = event.getSignDatas();
        for (SignData signData : signDatas) {
            String[] lines = signData.getLines();
            for (int i = 0; i < 4; i++) {
                lines[i] = lines[i]
                        .replace("{player}", event.getPlayer().getName());
            }
            signData.setLines(lines);
        }
    }
}
