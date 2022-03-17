package net.envirocraft.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class API {

    static final String apiURL = "https://us.api.blizzard.com/data/wow/guild/tichondrius/primo/roster?namespace=profile-us&locale=en_US&access_token=!token";

    static List<String> members = new LinkedList<>();

    public static void updateGuildMembers() {
        List<String> temp = new LinkedList<>();
        try {
            URL url = new URL(apiURL.replaceAll("!token", Config.get("blizzard_token").getAsString()));
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() != 200) {
                System.out.println(conn.getResponseMessage());
                return;
            }
            JsonObject response = new Gson().fromJson(new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8), JsonObject.class);
            for (JsonElement jsonElement : response.get("members").getAsJsonArray()) {
                temp.add(jsonElement.getAsJsonObject().get("character").getAsJsonObject().get("name").getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        members = temp;
    }

    public static List<String> getGuildMembers() {
        return members;
    }
}
