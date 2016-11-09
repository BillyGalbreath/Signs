package net.pl3x.bukkit.pl3xsigns.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class UpdateSignPacketEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Location location;
    private String[] lines;
    private boolean forceColor = false;

    public UpdateSignPacketEvent(Player player, String[] lines, Location location) {
        super(player);
        this.lines = lines;
        this.location = location;
    }

    public String[] getLines() {
        return lines;
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }

    public Location getLocation() {
        return location;
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
