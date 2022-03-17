package net.envirocraft.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Command extends ListenerAdapter {

    final String apiUrl = "https://raider.io/api/v1/characters/profile?region=us&realm=Tichondrius&name=!name&fields=mythic_plus_scores_by_season%3Acurrent%2Cmythic_plus_ranks";

    final Map<String, Color> CLASS_COLORS = new HashMap<>();
    public Command() {
        CLASS_COLORS.put("Death Knight", new Color(196, 30, 58));
        CLASS_COLORS.put("Demon Hunter", new Color(163, 48, 201));
        CLASS_COLORS.put("Druid", new Color(255, 124, 10));
        CLASS_COLORS.put("Hunter", new Color(170, 211, 144));
        CLASS_COLORS.put("Mage", new Color(63, 199, 235));
        CLASS_COLORS.put("Monk", new Color(0, 255, 152));
        CLASS_COLORS.put("Paladin", new Color(244, 140, 186));
        CLASS_COLORS.put("Priest", new Color(255, 255, 255));
        CLASS_COLORS.put("Rogue", new Color(255, 244, 104));
        CLASS_COLORS.put("Shaman", new Color(0, 112, 221));
        CLASS_COLORS.put("Warlock", new Color(135, 136, 238));
        CLASS_COLORS.put("Warrior", new Color(198, 155, 109));
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("raiderio")) return;
        event.deferReply().queue();
        String name = Objects.requireNonNull(event.getOption("name")).getAsString();
        try {
            URL url = new URL(apiUrl.replaceAll("!name", URLEncoder.encode(name, StandardCharsets.UTF_8)));
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() != 200) {
                System.out.println(apiUrl.replaceAll("!name", URLEncoder.encode(name, StandardCharsets.UTF_8)));
                System.out.println(conn.getResponseMessage());
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.RED);
                embedBuilder.setDescription("**Failed to get the player** `%s`**!**".formatted(name));
                event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                return;
            }
            JsonObject response = new Gson().fromJson(new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8), JsonObject.class);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(
                    response.get("name").getAsString(),
                    response.get("profile_url").getAsString(),
                    "https://cdnassets.raider.io/images/classes/class_%s.png".formatted(
                            response.get("class").getAsString().replaceAll(" ", "-").toLowerCase(Locale.ROOT)
                    )
            );

            String role = response.get("active_spec_role").getAsString().toLowerCase(Locale.ROOT);
            role = role.substring(0, 1).toUpperCase() + role.substring(1);

            String about =
                    "**Class:** %s\n".formatted(response.get("class").getAsString()) +
                    "**Spec:** %s\n".formatted(response.get("active_spec_name").getAsString()) +
                    "**Role:** %s\n".formatted(role) +
                    "**M+ Score:** %s\n".formatted(Math.round(Float.parseFloat(response.get("mythic_plus_scores_by_season").getAsJsonArray()
                            .get(0).getAsJsonObject().get("scores").getAsJsonObject().get("all").getAsString()))) +
                    "**Tichondrius Rank:** %s".formatted(response.get("mythic_plus_ranks").getAsJsonObject()
                            .get("class").getAsJsonObject().get("realm").getAsString());

            embedBuilder.setDescription(about);

            embedBuilder.setColor(CLASS_COLORS.get(response.get("class").getAsString()));

            embedBuilder.setTimestamp(Instant.parse(response.get("last_crawled_at").getAsString()));

            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

