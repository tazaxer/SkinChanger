package net.unpluggedmc.skinchanger;

import io.netty.PacketHandlerManager;
import net.unpluggedmc.skinchanger.Commands.CommandFoo;
import net.unpluggedmc.skinchanger.Commands.CommandNick;
import net.unpluggedmc.skinchanger.Commands.CommandSkin;
import net.unpluggedmc.skinchanger.Listeners.Join;
import net.unpluggedmc.skinchanger.Listeners.Quit;
import net.unpluggedmc.skinchanger.Listeners.SignEditComplete;
import net.unpluggedmc.skinchanger.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SkinChanger extends JavaPlugin {

    private Utils utils;
    private PacketHandlerManager packetHandlerManager;
    public Map<UUID, Boolean> editingSign;

    @Override
    public void onEnable() {
        new Join(this);
        new Quit(this);
        new SignEditComplete(this);
        new CommandNick(this);

        this.editingSign = new HashMap<>();
        // Plugin startup logic
        new CommandSkin(this);
        new CommandFoo(this);
        this.utils = new Utils(this);
        this.packetHandlerManager = new PacketHandlerManager();

        getLogger().info("SkinChanger enabled!");
    }

    public PacketHandlerManager getPacketHandlerManager() {
        return this.packetHandlerManager;
    }
    public Utils utils() {
        return utils;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
