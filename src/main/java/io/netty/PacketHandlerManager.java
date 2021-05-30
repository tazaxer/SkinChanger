package io.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelPipeline;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketHandlerManager {

    public PacketHandlerManager() {

    }

    public void injectPlayer(Player p) {

        ChannelDuplexHandler handler = new PlayerChannelDuplexHandler(p.getUniqueId());


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
