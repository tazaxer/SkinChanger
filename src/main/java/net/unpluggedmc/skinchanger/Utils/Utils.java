package net.unpluggedmc.skinchanger.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class Utils {

    private final SkinChanger plugin;

    public Utils(SkinChanger plugin) {
        this.plugin = plugin;
    }

    public void resetOnError(Player p) {
        CraftPlayer player = (CraftPlayer) p;

        player.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, player.getHandle()));

        performClientUpdate(p);
    }

    public void sendBlackScreenOfDeath(Player p) {

        CraftPlayer player = (CraftPlayer) p;

        player.getHandle().playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.e, 0));
    }

    public void performClientUpdate(Player p) {

        CraftPlayer player = (CraftPlayer) p;
        PlayerConnection connection = player.getHandle().playerConnection;

        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, player.getHandle()));

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

        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player.getHandle()));
    }

    public void performServerSideUpdate(Player p) {
        CraftPlayer player = (CraftPlayer) p;


        Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> currentPlayer.getUniqueId() != p.getUniqueId()).forEach(ps -> {

            ps.hidePlayer(plugin, player);

            Packet<?> tabChange = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, player.getHandle());
            ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(tabChange);

            player.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));


            Bukkit.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(player.getHandle()));
                ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player.getHandle()));

                ((CraftPlayer) ps).showPlayer(plugin, p);
            }, 5);

        });
    }

    public void performGeneralUpdate(Player p) {
        performServerSideUpdate(p);
        performClientUpdate(p);
    }
    public Property getSkin(UUID target) {

        String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + target.toString() + "?unsigned=false";

        try {
            URL site = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) site.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();

            InputStream input = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));

            StringBuffer buffer = new StringBuffer();

            String line;

            while ((line = reader.readLine()) != null) {

                buffer.append(line.trim());

            }

            reader.close();
            connection.disconnect();

            String result = buffer.toString();

            JsonObject object = (JsonObject) (new Gson()).fromJson(result, JsonObject.class);

            JsonArray array = object.getAsJsonArray("properties");

            String texture = new String(array.get(0).getAsJsonObject().get("value").getAsString());
            String signature = new String(array.get(0).getAsJsonObject().get("signature").getAsString());

            return new Property("textures", texture, signature);
        } catch (Exception x) {

            return null;
        }


    }

    public Property getVerifiedTextures(String name) {
        UUIDHelper uuidHelper = new UUIDHelper();
        UUID uuid = uuidHelper.nameToUUID(name);
        if (uuid == null) {
            return null;
        }
        Property textures = getSkin(uuid);

        if (textures == null) {
            return null;
        }

        return textures;
    }

    public void changeTextureFromProfile(GameProfile profile, Property textures) {
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", textures);
    }

    private Property testTextures() {
        return new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMDU0NzY0NDEzOCwKICAicHJvZmlsZUlkIiA6ICJiZDNkZDVhNDA0Mzg0Njk5YjJmZDM2ZjUxODE1NGI0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJHZW9yZ2VOb3RGb3VuZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yZDc1NTI2NzgwNTg3MjBmODkyMGJjZWU2ODJhYzRlNzQ3NWU0MWUyMTU1YWU2NzAwYjJhNTgzODlmNWI2NGY2IgogICAgfQogIH0KfQ==", "QomENv7nZHS5QTZSHm52neKU6eTQYWR7c/kIfDfRDJRrbSUPnuhNkvwpbW1jn5IsZc23Mrh2BEl16NJiUxM2sKQwMGIoIm2x0kR852jQ6SrgTZNHWUcMt5INMFwkqxKIq2vWNMmTcXTX/z9kX1sUUPMJQhlivSbmx6LqKWNRzW4l9TKsu+LIojJ50qmHh/4pNNGMn8h/HLaVQ1hrFYqkAeaCPIsqlq4uFiscRdWiEZ+eC9Jhs1SePE8t5lhFteBVMDqMMKWFTlt1VblGP5QnSc4ybkyjc/BFw2HA3qhsEfr4X50wLdfNtLDNiXdtIGLvvsWBXCOQY0Upf2p88ZpufB8Om9UGoGi3PRVeNm4JR8wtL4KxDGZXBCMOm88foyqQ1Z1q59BK/GKtLymYXddyXLNw4hPv4ZXyKLVVJEfX9r35fk5OFk6GQFuvHEJpT7bAqKQ15kE1nIDfWLCLyC69QFmDV5ph9exj4vB6RAwlGdApn7VSWAF/GTO76irJl9NE06WQLeQLGYXgFgSWEtCC6wngBlVXZ0iCbjfbneu5oYCAhA/vzkiCrX7ApcJBXeVFHNX9FF0m7PA6BtXEyg1RhCoZJ3eNv42pnGGt+dsvGFWW8lZtCWbDKSZUQ+AUGZMipCfpdYzPczvWmi90lrVhPB/71MmBasJuOqWj3odV55g=");
    }
}
