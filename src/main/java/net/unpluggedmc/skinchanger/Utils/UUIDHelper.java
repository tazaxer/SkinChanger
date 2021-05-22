package net.unpluggedmc.skinchanger.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDHelper {

    public String addCharToString(String str, char c, int pos) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.insert(pos, c);

        return stringBuilder.toString();
    }

    public String formatUUID(String trimmedUUID) throws IllegalArgumentException {
        if (trimmedUUID == null) throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        /* Backwards adding to avoid index adjustments */
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }

        return builder.toString();
    }

    public String getUUID(String body) {
        final Pattern pattern = Pattern.compile("id\":\"(.*?)\"}");
        final Matcher matcher = pattern.matcher(body);

        matcher.find();

        String id = matcher.group(1);
        String uuid = formatUUID(id);

        return uuid;
    }

    public UUID nameToUUID(String name) {
        try {

            URL site = new URL("https://api.mojang.com/users/profiles/minecraft/" + name  );

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

            String idString = object.get("id").getAsString();

            String uuidString = formatUUID(idString);

            return UUID.fromString(uuidString);
        } catch (Exception e) {
            return null;
        }

    }
}