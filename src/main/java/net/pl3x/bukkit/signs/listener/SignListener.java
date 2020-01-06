package net.pl3x.bukkit.signs.listener;

import net.pl3x.bukkit.signs.configuration.Lang;
import net.pl3x.bukkit.signs.event.LoadSignPacketEvent;
import net.pl3x.bukkit.signs.event.UpdateSignPacketEvent;
import net.pl3x.bukkit.signs.manager.Editor;
import net.pl3x.bukkit.signs.manager.History;
import net.pl3x.bukkit.signs.protocollib.SignData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
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
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class SignListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSignEditLowest(SignChangeEvent event) {
        if (event.getPlayer().hasPermission("sign.color")) {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
            }
        }
        Editor.close((Sign) event.getBlock().getState());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignEditMonitor(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        History.get(sign).add(sign.getLines());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void signPlace(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }

        if (!player.hasPermission("click.signedit")) {
            return;
        }

        Material type = player.getInventory().getItemInMainHand().getType();
        if (!(Tag.SIGNS.isTagged(type) || Tag.WALL_SIGNS.isTagged(type) || Tag.STANDING_SIGNS.isTagged(type))) {
            return;
        }

        Block block = event.getClickedBlock();
        if (!(block.getState() instanceof Sign)) {
            return;
        }

        // always cancel event here
        event.setCancelled(true);

        Sign sign = (Sign) block.getState();
        if (Editor.isOpen(sign)) {
            Lang.send(player, Lang.SIGN_ALREADY_OPEN);
            return;
        }

        if (!new BlockBreakEvent(block, player).callEvent()) {
            Lang.send(player, Lang.CANNOT_EDIT_SIGN);
            return;
        }

        Editor.open(player, sign);
    }

    @EventHandler
    public void onSignUpdatePacket(UpdateSignPacketEvent event) {
        String[] lines = event.getLines();
        for (int i = 0; i < 4; i++) {
            lines[i] = lines[i].replace("{player}", event.getPlayer().getName());
        }
        event.setLines(lines);
    }

    @EventHandler
    public void onSignLoadPacket(LoadSignPacketEvent event) {
        List<SignData> signDatas = event.getSignDatas();
        for (SignData signData : signDatas) {
            String[] lines = signData.getLines();
            for (int i = 0; i < 4; i++) {
                lines[i] = lines[i].replace("{player}", event.getPlayer().getName());
            }
            signData.setLines(lines);
        }
    }
}
