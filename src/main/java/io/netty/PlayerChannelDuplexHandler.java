package io.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayInUpdateSign;
import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerChannelDuplexHandler extends ChannelDuplexHandler {

    private UUID player;
    private SkinChanger plugin;

    public PlayerChannelDuplexHandler(SkinChanger plugin, UUID player) {
        this.player = player;
        this.plugin = plugin;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {

        if (!(packet instanceof PacketPlayInUpdateSign)) {
            super.channelRead(ctx, packet);
            return;
        }

        PacketPlayInUpdateSign updateSign = (PacketPlayInUpdateSign) packet;

        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new PlayerCompleteSignEditEvent(player, updateSign)));

        Bukkit.getLogger().info("EVENT FIRED!");
        super.channelRead(ctx, packet);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }
}
