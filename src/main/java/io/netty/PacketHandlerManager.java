package io.netty;

import io.netty.channel.*;
import net.minecraft.server.v1_16_R3.PacketPlayInUpdateSign;
import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketHandlerManager {

    private SkinChanger plugin;

    public PacketHandlerManager(SkinChanger plugin) {
        this.plugin = plugin;
    }

    public void injectPlayer(Player p) {

        ChannelDuplexHandler handler = new PlayerChannelDuplexHandler;


        ChannelPipeline pipeline = ((CraftPlayer)p).getHandle().playerConnection.networkManager.channel.pipeline();

        pipeline.addBefore("packet_handler", p.getName(), handler);
    }

    public void removePlayer(Player p) {
        Channel channel = ((CraftPlayer)p).getHandle().playerConnection.networkManager.channel;

        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(p.getName());
            return null;
        });
    }
}
