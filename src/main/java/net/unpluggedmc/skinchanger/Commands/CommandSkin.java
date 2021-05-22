package net.unpluggedmc.skinchanger.Commands;

import com.mojang.authlib.properties.Property;
import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CommandSkin implements CommandExecutor {

    private SkinChanger plugin;

    public CommandSkin(SkinChanger plugin) {
        this.plugin = plugin;
        plugin.getCommand("skin").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Wrong Syntax! /skin <skinOwner> <targetPlayer>");
                return true;
            }

            Player p = null;
            String target = args[0];

            try {
                p = Bukkit.getPlayer(args[1]);
            } catch (Exception x) {
                sender.sendMessage(ChatColor.RED + "Specified target player not found!");
                return true;
            }

            CraftPlayer player = ((CraftPlayer) p);

            p.sendMessage("§8(§7Your skin is getting changed§8) §6Processing result! Please wait...");
            plugin.utils().sendBlackScreenOfDeath(p);

            Property textures = plugin.utils().getVerifiedTextures(target);
            if (textures == null) {
                p.sendMessage(ChatColor.RED + "Error Contacting Mojang Servers! Please check your spelling and try again.");
                plugin.utils().resetOnError(p);
                return true;
            }

            plugin.utils().changeTextureFromProfile(player.getProfile(), textures);
            plugin.utils().performGeneralUpdate(player);

            p.sendMessage(ChatColor.DARK_GREEN + "Your skin was successfully changed §6by CONSOLE!");
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        Player p = null;
        String target = args[0];

        if (args.length == 2) {
            if (args[1] != null) {

                try {
                    p = Bukkit.getPlayer(args[1]);
                } catch (Exception x) {
                    sender.sendMessage(ChatColor.RED + "The target player doesn't exist!");
                }

            } else {
                p = (Player) sender;
            }
        } else if (args.length == 1) {
            p = (Player) sender;
        }


        CraftPlayer player = ((CraftPlayer) p);

        p.sendMessage("§6Processing result! Please wait...");
        plugin.utils().sendBlackScreenOfDeath(p);

        Property textures = plugin.utils().getVerifiedTextures(target);
        if (textures == null) {
            p.sendMessage(ChatColor.RED + "Error Contacting Mojang Servers! Please check your spelling and try again.");
            plugin.utils().resetOnError(p);
            return true;
        }

        plugin.utils().changeTextureFromProfile(player.getProfile(), textures);
        plugin.utils().performGeneralUpdate(player);

        p.sendMessage(args.length == 1? ChatColor.DARK_GREEN + "Your skin was successfully changed!" : ChatColor.DARK_GREEN + "Your skin was successfully changed §6by " + sender.getName() + "!");
        return true;
    }






}
