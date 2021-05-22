package net.unpluggedmc.skinchanger.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class TextureParser {

    public String getSkin(String uuid) {
        URL mojang = null;
        try {
            mojang = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" +
                    uuid + "?unsigned=false");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(mojang.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        return textureProperty.get("value").getAsString();
    }

    public String getSignature(String uuid) {
        URL mojang = null;
        try {
            mojang = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" +
                    uuid + "?unsigned=false");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(mojang.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        return textureProperty.get("signature").getAsString();
    }
}
