package net.unpluggedmc.skinchanger.Listeners;

import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

    private SkinChanger plugin;

    public Quit(SkinChanger plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getPacketHandlerManager().removePlayer(e.getPlayer());
    }
}
