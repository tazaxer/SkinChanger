package net.unpluggedmc.skinchanger;

import net.unpluggedmc.skinchanger.Commands.CommandFoo;
import net.unpluggedmc.skinchanger.Commands.CommandSkin;
import net.unpluggedmc.skinchanger.Utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkinChanger extends JavaPlugin {

    private Utils utils;

    @Override
    public void onEnable() {
        // Plugin startup logic
        new CommandSkin(this);

        this.utils = new Utils(this);
    }

    public Utils utils() {
        return utils;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}