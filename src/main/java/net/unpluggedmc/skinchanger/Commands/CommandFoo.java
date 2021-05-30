package net.unpluggedmc.skinchanger.Commands;

import com.google.gson.JsonObject;
import net.minecraft.server.v1_16_R3.*;
import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.block.impl.CraftFloorSign;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.Colorable;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

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

        //INITIALIZE PLAYER AND GET INSTANCE OF CONNECTION
        Player p = (Player) sender;
        PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;

        //SUBTRACT THE LOCATION BY 1 (TODO: wrong way fix asap)
        Location loc = new Location(p.getLocation().getWorld(), p.getLocation().getX()-1, p.getLocation().getY(), p.getLocation().getZ()-1);

        BlockData data = Bukkit.createBlockData(Material.OAK_SIGN);

        PacketPlayOutBlockChange blockChange = new PacketPlayOutBlockChange(((CraftWorld)p.getWorld()).getHandle(), new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));
        blockChange.block = ((CraftBlockData)data).getState();

        connection.sendPacket(blockChange);

        p.sendSignChange(loc, new String[]{"", "^^^^", "Enter Your", "Nickname Here!"});
        //PREPARE SIGN EDITOR
        PacketPlayOutOpenSignEditor signEditor = new PacketPlayOutOpenSignEditor(new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));

        connection.sendPacket(signEditor); //SEND SIGN EDITOR
        plugin.getLogger().info("Sign Editor Sent!");
        return true;
    }
}
