package net.envirocraft.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class API {

    static final String rosterAPI = "https://us.api.blizzard.com/data/wow/guild/tichondrius/primo/roster?namespace=profile-us&locale=en_US&access_token=!token";
    static final String userAPI = "https://raider.io/api/v1/characters/profile?region=us&realm=Tichondrius&name=!name&fields=mythic_plus_scores_by_season%3Acurrent%2Cmythic_plus_ranks";

    static List<String> members = new LinkedList<>();

    public static String getBlizzardAPIToken() {
        String encodedCredentials = Base64.getEncoder().encodeToString(String.format("%s:%s", Config.get("blizzard_id").getAsString(), Config.get("blizzard_secret").getAsString()).getBytes(StandardCharsets.UTF_8));
        try {
            URL url = new URL("https://us.battle.net/oauth/token");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", String.format("Basic %s", encodedCredentials));
            con.setDoOutput(true);
            con.getOutputStream().write("grant_type=client_credentials".getBytes(StandardCharsets.UTF_8));

            return new Gson().fromJson(new String(con.getInputStream().readAllBytes()), JsonObject.class).get("access_token").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Updates the local copy of the list of guild members
     */
    public static void updateGuildMembers() {
        List<String> temp = new LinkedList<>();
        try {
            URL url = new URL(rosterAPI.replaceAll("!token", getBlizzardAPIToken()));
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() != 200) {
                System.out.println("it was me");
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
        temp.sort(String::compareToIgnoreCase);
        members = temp;
    }

    /**
     * Get the {@link List} of cached guild members
     * @return A {@link List} of the guild members names
     */
    public static @NotNull List<String> getGuildMembers() {
        return members;
    }

    /**
     * Get a player from raider.io
     * @param name The name of the player to get
     * @return     A {@link JsonObject} containing the response from raider.io
     *         <br>Here is an <a href="https://raider.io/api/v1/characters/profile?region=us&realm=Tichondrius&name=lemonjoey&fields=mythic_plus_scores_by_season%3Acurrent%2Cmythic_plus_ranks">example response</a>.
     */
    public static @NotNull JsonObject getPlayer(@NotNull String name) {
        JsonObject player = new JsonObject();
        try {
            URL url = new URL(userAPI.replaceAll("!name", URLEncoder.encode(name, StandardCharsets.UTF_8)));
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() != 200) {
                System.out.println(userAPI.replaceAll("!name", URLEncoder.encode(name, StandardCharsets.UTF_8)));
                System.out.println(conn.getResponseMessage());
                return player;
            }
            player = new Gson().fromJson(new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return player;
    }
}
