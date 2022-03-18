package net.envirocraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.envirocraft.utils.API;
import net.envirocraft.utils.Config;
import net.envirocraft.utils.MPlusComparator;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DynamicMessage {

    static File contextFile = new File(Bot.localPath + File.separator + "scores.json");
    static long lastChecked = 0;

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
                TimeUnit.MILLISECONDS.sleep(250);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        players.sort(new MPlusComparator());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Guild Raider.io Ranks")
                .setColor(Color.GREEN);

        Map<String, Integer> context = getContext();

        int count = 0;
        for (JsonObject player : players) {
            if (count++ >= 30) break;
            embedBuilder.appendDescription("**%s:** %s %s (+%s)\n".formatted(
                    count == 1 ? "\uD83E\uDD47" : count == 2 ? "\uD83E\uDD48" : count == 3 ? "\uD83E\uDD49" : count,
                    player.get("name").getAsString(),
                    (int) player.get("mythic_plus_scores_by_season").getAsJsonArray()
                            .get(0).getAsJsonObject()
                            .get("scores").getAsJsonObject()
                            .get("all").getAsFloat(),
                    (int) player.get("mythic_plus_scores_by_season").getAsJsonArray()
                            .get(0).getAsJsonObject()
                            .get("scores").getAsJsonObject()
                            .get("all").getAsFloat() - context.getOrDefault(player.get("name").getAsString(), 0)
            ));
        }
        embedBuilder.appendDescription("\nCompared against: <t:%s:f>".formatted(lastChecked));
        embedBuilder.setFooter("Took: "+ ((float)(System.currentTimeMillis() - startTime)) / 1000 / 60 + " min");

        channel.sendMessageEmbeds(embedBuilder.build()).queue();

        // Save scores
        save(players);
    }

    public static void save(@NotNull List<JsonObject> players) {
        try {
            JsonObject saveData = new JsonObject();
            for (JsonObject player : players) {
                saveData.addProperty(
                        player.get("name").getAsString(),
                        player.get("mythic_plus_scores_by_season").getAsJsonArray()
                                .get(0).getAsJsonObject()
                                .get("scores").getAsJsonObject()
                                .get("all").getAsFloat());
            }
            JsonObject fullObject = new JsonObject();
            fullObject.addProperty("time", Instant.now().getEpochSecond());
            fullObject.add("players", saveData);
            contextFile.createNewFile();
            new FileOutputStream(contextFile).write(new GsonBuilder().setPrettyPrinting().create().toJson(fullObject).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static @NotNull Map<String, Integer> getContext() {
        Map<String, Integer> context = new HashMap<>();
        try {
            JsonObject data = new Gson().fromJson(new String(new FileInputStream(contextFile).readAllBytes()), JsonObject.class);
            lastChecked = data.get("time").getAsLong();
            for (String name : data.get("players").getAsJsonObject().keySet()) {
                context.put(name, (int) data.get("players").getAsJsonObject().get(name).getAsFloat());
            }
        } catch (IOException ignored) {}
        return context;
    }
}
