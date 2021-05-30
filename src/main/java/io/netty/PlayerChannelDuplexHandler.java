package io.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayInUpdateSign;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerChannelDuplexHandler extends ChannelDuplexHandler {

    private UUID player;

    public PlayerChannelDuplexHandler(UUID player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {

        if (!(packet instanceof PacketPlayInUpdateSign)) {
            super.channelRead(ctx, packet);
            return;
        }

        PacketPlayInUpdateSign updateSign = (PacketPlayInUpdateSign) packet;

        Bukkit.getLogger().info("UPDATE SIGN PACKET READ!");
        PlayerCompleteSignEditEvent event = new PlayerCompleteSignEditEvent(player, updateSign);
        Bukkit.getPluginManager().callEvent(event);
        Bukkit.getLogger().info("EVENT FIRED!");
        super.channelRead(ctx, packet);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }
}
