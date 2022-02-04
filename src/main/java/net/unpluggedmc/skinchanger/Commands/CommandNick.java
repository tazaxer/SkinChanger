package net.unpluggedmc.skinchanger.Commands;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CommandNick implements CommandExecutor {

    private SkinChanger plugin;

    public CommandNick(SkinChanger plugin) {
        this.plugin = plugin;
        plugin.getCommand("nick").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may execute this command! (At least for now)");
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

            openSignEditor(p);

            return true;
            //SEND SIGN EDITOR
        }

        if (!plugin.utils().isMcName(nickname)) {
            sender.sendMessage(ChatColor.RED + "Nickname can only contain letters and numbers!");
            return true;
        }

        if (nickname.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Nickname can be up to 16 characters!");
            return true;
        }

        if (nickname.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Nickname has to be more than 3 characters!");
            return true;
        }

        Player bukkitPlayer = targetPlayer;
        CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;


        plugin.utils().setNickname(bukkitPlayer, nickname);
        plugin.utils().sendNicknameUpdate(craftPlayer);


        return true;
    }

    public void openSignEditor(Player p) {

        CraftPlayer cp = (CraftPlayer) p;
        PlayerConnection connection = cp.getHandle().playerConnection;

        Location loc1;

        //SUBTRACT THE LOCATION BY 1 (TODO: wrong way fix asap)
        Location loc = new Location(p.getLocation().getWorld(), p.getLocation().getX()-1, p.getLocation().getY(), p.getLocation().getZ()-1);

        BlockData data = Bukkit.createBlockData(Material.OAK_SIGN);

        PacketPlayOutBlockChange blockChange = new PacketPlayOutBlockChange(((CraftWorld)p.getWorld()).getHandle(), new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));
        blockChange.block = ((CraftBlockData)data).getState();

        connection.sendPacket(blockChange);

        p.sendSignChange(loc, new String[]{"", "^^^^^^^^^^^^^", "Enter Your", "Nickname Here!"});
        //PREPARE SIGN EDITOR
        PacketPlayOutOpenSignEditor signEditor = new PacketPlayOutOpenSignEditor(new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));

        connection.sendPacket(signEditor); //SEND SIGN EDITOR

        plugin.editingSign.put(p.getUniqueId(), true);
    }


}
