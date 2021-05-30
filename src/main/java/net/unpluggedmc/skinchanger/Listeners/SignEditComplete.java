package net.unpluggedmc.skinchanger.Listeners;

import com.mojang.authlib.GameProfile;
import io.netty.PlayerCompleteSignEditEvent;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockBreak;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_16_R3.WorldServer;
import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

public class SignEditComplete implements Listener {

    private SkinChanger plugin;


    public SignEditComplete(SkinChanger plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSign(PlayerCompleteSignEditEvent e) {
        if (!plugin.editingSign.get(e.getPlayer().getUniqueId())) {
            return;
        }

        String name = e.getLines().get(0);

        if (name.isEmpty()) {
            e.getPlayer().sendMessage("§cPlease specify a nickname!");
            return;
        }

        e.getPlayer().performCommand("nick " + name);

        plugin.editingSign.remove(e.getPlayer().getUniqueId());
        e.getPlayer().sendBlockChange(e.getSignLocation(), Bukkit.createBlockData(Material.AIR));
    }




}