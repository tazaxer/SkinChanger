package io.netty;

import net.minecraft.server.v1_16_R3.PacketPlayInUpdateSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class PlayerCompleteSignEditEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final UUID player;
    private final String[] lines;
    private final Location location;


    public PlayerCompleteSignEditEvent(UUID player, PacketPlayInUpdateSign packet) {
        super(true);
        this.player = player;
        this.lines = packet.c();
        this.location = new Location(Bukkit.getPlayer(player).getWorld(), packet.b().getX(), packet.b().getY(), packet.b().getZ());
    }

    public Location getSignLocation() {
        return location;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public List<String> getLines() {
        return Arrays.asList(lines);
    }
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
