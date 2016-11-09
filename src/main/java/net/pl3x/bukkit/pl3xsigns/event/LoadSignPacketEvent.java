package net.pl3x.bukkit.pl3xsigns.event;

import net.pl3x.bukkit.pl3xsigns.SignData;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.List;

public class LoadSignPacketEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final List<SignData> signDatas;
    private boolean forceColor = false;

    public LoadSignPacketEvent(Player player, List<SignData> signDatas) {
        super(player);
        this.signDatas = signDatas;
    }

    public List<SignData> getSignDatas() {
        return signDatas;
    }

    public boolean isForceColor() {
        return forceColor;
    }

    public void setForceColor(boolean forceColor) {
        this.forceColor = forceColor;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
