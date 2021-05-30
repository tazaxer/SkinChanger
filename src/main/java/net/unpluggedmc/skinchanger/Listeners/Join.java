package net.unpluggedmc.skinchanger.Listeners;

import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {

    private SkinChanger plugin;

    public Join(SkinChanger plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getPacketHandlerManager().injectPlayer(e.getPlayer());
    }
}
