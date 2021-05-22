package net.unpluggedmc.skinchanger.Commands;

import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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


        return true;
    }
}
