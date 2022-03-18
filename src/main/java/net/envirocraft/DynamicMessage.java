package net.envirocraft;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.envirocraft.utils.API;
import net.envirocraft.utils.Config;
import net.envirocraft.utils.MPlusComparator;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DynamicMessage {

    //private static JsonObject data = new JsonObject();

    public static void update() {

        long startTime = System.currentTimeMillis();

        // Get the channel and message
        TextChannel channel = Bot.jda.getTextChannelById(Config.get("overview_channel").getAsString());
        if (channel == null) {
            System.out.println("[Warning] Could not get overview channel!");
            return;
        }

        while (API.getGuildMembers().size() == 0) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<JsonObject> players = new LinkedList<>();
        try {
            int i = 0;
            for (String name : API.getGuildMembers()) {
                i++;
                JsonObject player = API.getPlayer(name);
                System.out.printf("Checked %s/%s%n", i, API.getGuildMembers().size());
                if (!player.equals(new JsonObject()))
                    players.add(player);
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        players.sort(new MPlusComparator());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Guild Raider.io Ranks")
                .setColor(Color.GREEN);

        int count = 0;
        for (JsonObject player : players) {
            if (count++ > 25) break;
            embedBuilder.appendDescription("**%s:** %s %s\n".formatted(
                    count,
                    player.get("name").getAsString(),
                    player.get("mythic_plus_scores_by_season").getAsJsonArray()
                            .get(0).getAsJsonObject()
                            .get("scores").getAsJsonObject()
                            .get("all").getAsFloat()
            ));
        }
        embedBuilder.setFooter("\n Took: "+ ((float)(System.currentTimeMillis() - startTime)) / 1000 / 60 + " min\"");

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
