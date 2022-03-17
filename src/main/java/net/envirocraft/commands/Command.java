package net.envirocraft.commands;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.envirocraft.utils.API;
import net.envirocraft.utils.StringUtil;
import net.envirocraft.utils.WoWClass;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Command extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("raiderio")) return;
        event.deferReply().queue();
        String name = Objects.requireNonNull(event.getOption("name")).getAsString();
        JsonObject player = API.getPlayer(name);
        if (Objects.equals(player, new JsonObject())) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("**Failed to get the player** `%s`**!**".formatted(name));
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(
                player.get("name").getAsString(),
                player.get("profile_url").getAsString(),
                "https://cdnassets.raider.io/images/classes/class_%s.png".formatted(
                        player.get("class").getAsString().replaceAll(" ", "-").toLowerCase(Locale.ROOT)
                )
        );

        String about =
                "**Class:** %s\n".formatted(player.get("class").getAsString()) +
                "**Spec:** %s\n".formatted(player.get("active_spec_name").getAsString()) +
                "**Role:** %s\n".formatted(StringUtil.capitalize(player.get("active_spec_role").getAsString())) +
                "**M+ Score:** %s\n".formatted(Math.round(Float.parseFloat(player.get("mythic_plus_scores_by_season").getAsJsonArray()
                        .get(0).getAsJsonObject().get("scores").getAsJsonObject().get("all").getAsString()))) +
                "**Tichondrius Rank:** %s".formatted(player.get("mythic_plus_ranks").getAsJsonObject()
                        .get("class").getAsJsonObject().get("realm").getAsString());

        embedBuilder.setDescription(about);

        embedBuilder.setColor(WoWClass.valueOf(player.get("class").getAsString().replaceAll(" ", "_")).getColor());

        embedBuilder.setTimestamp(Instant.parse(player.get("last_crawled_at").getAsString()));

        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();

    }

}

