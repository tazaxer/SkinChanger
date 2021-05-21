package net.unpluggedmc.skinchanger.Utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import net.unpluggedmc.skinchanger.SkinChanger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class Utils {

    private final SkinChanger plugin;

    public Utils(SkinChanger plugin) {
        this.plugin = plugin;
    }

    public void performClientUpdate(Player p) {

        CraftPlayer player = (CraftPlayer) p;
        PlayerConnection connection = player.getHandle().playerConnection;

        player.setPlayerListName(player.getName());

        Location loc = p.getLocation().clone();
        HashMap<Integer, ItemStack> items = new HashMap<>();
        ItemStack[] armor = p.getInventory().getArmorContents().clone();

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            items.put(i, p.getInventory().getItem(i));
        }

        boolean fly = p.isFlying();
        float xp = p.getExp();
        double health = p.getHealth();
        double healthScale = p.getHealthScale();
        int level = p.getLevel();
        int firetix = p.getFireTicks();
        int heldSlot = p.getInventory().getHeldItemSlot();

        p.getInventory().clear();

        WorldServer nmsWorld = ((CraftWorld)player.getWorld()).getHandle();



        Packet<?> preRespawn =
                new PacketPlayOutRespawn
                        (nmsWorld.getDimensionManager(), //dimanager
                                nmsWorld.getDimensionKey(), //dikey
                                BiomeManager //biomeData
                                        .a(nmsWorld.getSeed()), //seed
                                player.getHandle().playerInteractManager.getGameMode(), //gm
                                player.getHandle().playerInteractManager.c(), //previous gm
                                nmsWorld.isDebugWorld(), //debugornot?
                                nmsWorld.isFlatWorld(), //flatornot?
                                true); //copyMetaData?




        player.getHandle().playerConnection.sendPacket(preRespawn);

        player.getHandle().playerConnection.sendPacket(new PacketPlayOutViewDistance(nmsWorld.spigotConfig.viewDistance));
        player.getHandle().spawnIn(nmsWorld);
        player.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnPosition(new BlockPosition(loc.getX(), loc.getY(), loc.getZ()), nmsWorld.v()));

        player.getHandle().playerConnection.teleport(loc);

        player.setFlying(fly);
        player.setExp(xp);
        player.setHealth(health);
        player.setHealthScale(healthScale);
        player.setLevel(level);
        player.setFireTicks(firetix);
        player.getInventory().setHeldItemSlot(heldSlot);

        player.getInventory().setArmorContents(armor);
        items.forEach((integer, itemStack) -> {
            player.getInventory().setItem(integer, itemStack);
        });
    }

    public void performServerSideUpdate(Player p) {
        CraftPlayer craftPlayer = (CraftPlayer) p;

        Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> currentPlayer.getUniqueId() != p.getUniqueId()).forEach(ps -> {
            ps.hidePlayer(plugin, p);

            PacketPlayOutPlayerInfo pr = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer.getHandle());
            ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(pr);
            PacketPlayOutEntityDestroy d = new PacketPlayOutEntityDestroy(p.getEntityId());
            ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(d);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                PacketPlayOutPlayerInfo pa = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle());
                ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(pa);
                PacketPlayOutNamedEntitySpawn ns = new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle());
                ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(ns);

                ps.showPlayer(plugin, p);
            }, 5);
        });
    }

    public void performGeneralUpdate(Player p) {
        performServerSideUpdate(p);
        performClientUpdate(p);
    }

    public GameProfile constructGameProfile(String name) {
        GameProfile profile = new GameProfile(UUID.fromString("21ac0e4a-fa9e-4213-aae8-b6f906025062"), "§f[§cYOUTUBE§f] §7DennisUnplugged");

        profile.getProperties().removeAll("textures");

        profile.getProperties().put("textures", new Property("textures",
                "ewogICJ0aW1lc3RhbXAiIDogMTYyMTE2Mzk3MjExOSwKICAicHJvZmlsZUlkIiA6ICIyMWFjMGU0YWZhOWU0MjEzYWFlOGI2ZjkwNjAyNTA2MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJEZW5uaXNVbnBsdWdnZWQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTIyZjc4YThmNTc1ZTliOGRlYzhiZGQ4ZGM2MDUzZjYyYTBmZWNlZmExYzg5MzAxMTgwYzlkNjdkZWJhMjhhNCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                "TGthesYWPa6VIC5dX+QeqbwIXFDutYJtb8LsTMhBCZV5W41/a5VZjIrX5MeuMmnHeswcczNqi+8mBx0bnK6osuH1C2wXvlkbEfVwshD1f1gEFZYELCxzKc0qk0W1aYx2I1i96b3oHTPul3QdmDlVBm0qxEesun7iFK7Co7uB9F4qcHfXh5IcVPTu3U/3Uhd60WHw31Exuj7oCIa/1Xgdhnnwj8Rw8gY2gO5qTYcy74pCIoKssDftwmkzB6igN9lT/f8fh7eH8+e8raZhUh5f6j8S+9eyDx2ahLYA8eaU6W2WOY3QgKPAwI7G/1os1OvopHV6me5zxdNToSAALw59SVeZkrVZ7119SZ/a+/2r/o/z5DVB6hrbAceo4krum7A+lV0SCwvFk2odkHTghgkQNpbSQyW46T+mjmYxZkbZW2KYJhnRzo7kQAzlTxMnlTLYwWyTWJ/CMRHSJ+DzGswJ5WP9aQlhxMl1CT5EkMYVyyRw8vHJbdWFnBTQrSGwiltzbs9cCwP8RkAiBQAtC5dBtydwG8c2FaTHKjTKOxUU0uTQ9Gt7yoDVuDDI9KLycNgHGdvwuSGvBLHgBdfmZGr2DC7dml0WN+SmlrXrpNQUfNHGyee40vSUOy5nNbdP4IfJrYZcaUZugQ52UuGKiBmlGxIu+Z571kRhdSGCbvunni8="));

        return profile;
    }
}
