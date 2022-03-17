package net.envirocraft;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.envirocraft.commands.Command;
import net.envirocraft.commands.CommandAutoComplete;
import net.envirocraft.utils.API;
import net.envirocraft.utils.Config;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.URISyntaxException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Bot {

    public static JDA jda = null;
    public static String localPath = null;
    public static Guild guild = null;

    public static void main(String[] args) throws LoginException, URISyntaxException, InterruptedException {

        long startTime = System.currentTimeMillis();

        System.out.println("Starting Bot...");

        File jarFile = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        localPath = jarFile.getParent() + File.separator;

        Config.load();

        jda = JDABuilder.createDefault(Config.get("token").getAsString())
                .addEventListeners(new Command())
                .addEventListeners(new CommandAutoComplete())
                .build();

        jda.awaitReady();

        guild = jda.getGuildById(Config.get("guild").getAsString());

        // TODO: 16/03/2022 Compare against last week
        if (guild == null) {
            System.err.println("Failed to get guild!");
            return;
        }
        // TODO: 16/03/2022 User context command
        // Create commands
        guild.updateCommands()
                .addCommands(
                        Commands.slash(
                                "raiderio",
                                "Get raider.io information for a player."
                        ).addOption(
                                OptionType.STRING,
                                "name",
                                "The name of the player.",
                                true,
                                true
                        )
                )
                .queue();

        // Start caching
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleAtFixedRate(API::updateGuildMembers, 0, 2, TimeUnit.MINUTES);

        System.out.println("Finished Loading! (" + ((float)(System.currentTimeMillis() - startTime)) / 1000 + " sec)");
    }
}
