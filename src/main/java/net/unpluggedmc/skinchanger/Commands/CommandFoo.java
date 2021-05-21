package net.unpluggedmc.skinchanger.Commands;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class CommandFoo implements CommandExecutor {

    private SkinChanger plugin;

    public CommandFoo(SkinChanger plugin) {
        this.plugin = plugin;

        plugin.getCommand("foo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may execute this command!");
            return true;
        }

        Player bukkitPlayer = (Player) sender;
        CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
        EntityHuman nmsPlayer = craftPlayer.getHandle();

        GameProfile profile = plugin.utils().constructGameProfile("foo");

        Field gpField;

        try {
            gpField = nmsPlayer.getClass().getSuperclass().getDeclaredField("bJ");
            gpField.setAccessible(true);

            gpField.set(nmsPlayer, profile);

            gpField.setAccessible(false);
        } catch (Exception x) {
            x.printStackTrace();
        }


        plugin.utils().performGeneralUpdate(bukkitPlayer);

        return true;
    }
}
