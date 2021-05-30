package net.unpluggedmc.skinchanger.Commands;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class CommandNick implements CommandExecutor {

    private SkinChanger plugin;

    public CommandNick(SkinChanger plugin) {
        this.plugin = plugin;
        plugin.getCommand("nick").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may execute this command!");
            return true;
        }

        String nickname = null;
        Player targetPlayer = null;

        if (args.length == 1) {
            nickname = args[0];
            targetPlayer = (Player) sender;
        } else if (args.length == 2) {
            nickname = args[0];
            targetPlayer = Bukkit.getPlayer(args[1]);
        } else if (args.length == 0) {
            Player p = (Player) sender;

            //SEND SIGN EDITOR
        }

        if (!checkChars(nickname)) {
            sender.sendMessage(ChatColor.RED + "Nickname can only contain letters and numbers!");
            return true;
        }

        if (nickname.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Nickname can only be up to 16 characters!");
            return true;
        }

        Player bukkitPlayer = targetPlayer;
        CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
        EntityHuman nmsPlayer = craftPlayer.getHandle();

        Field gameProfileField = null;

        try {
            gameProfileField = EntityHuman.class.getDeclaredField("bJ");
            gameProfileField.setAccessible(true);

            gameProfileField.set(nmsPlayer, new GameProfile(bukkitPlayer.getUniqueId(), nickname));
        } catch (Exception x) {
            x.printStackTrace();
        }

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle());

        craftPlayer.getHandle().playerConnection.sendPacket(packet);

        Bukkit.getOnlinePlayers().stream().filter(currentplayer -> currentplayer.getUniqueId() != bukkitPlayer.getUniqueId()).forEach(ps -> {
            ps.hidePlayer(plugin, craftPlayer);

            ((CraftPlayer) ps).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(craftPlayer.getEntityId()));
            ((CraftPlayer) ps).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle()));
            ((CraftPlayer) ps).getHandle().playerConnection.sendPacket(packet);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                ((CraftPlayer) ps).showPlayer(plugin, craftPlayer);

            }, 5);
        });


        return true;
    }

    private boolean checkChars(String s) {
        if (s == null) // checks if the String is null {
            return false;

        int len = s.length();
        for (int i = 0; i < len; i++) {
            // checks whether the character is neither a letter nor a digit
            // if it is neither a letter nor a digit then it will return false
            if ((Character.isLetterOrDigit(s.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }


}
