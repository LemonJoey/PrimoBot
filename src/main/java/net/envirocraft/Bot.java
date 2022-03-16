package net.envirocraft;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.envirocraft.utils.Config;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.URISyntaxException;

public class Bot {

    public static JDA jda = null;
    public static String localPath = null;
    public static Guild guild = null;

    public static void main(String[] args) throws LoginException, URISyntaxException, InterruptedException {

        File jarFile = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        localPath = jarFile.getParent() + File.separator;

        Config.load();

        jda = JDABuilder.createDefault(Config.get("token").getAsString())
                .addEventListeners(new Command())
                .build();

        jda.awaitReady();

        guild = jda.getGuildById(Config.get("guild").getAsString());

        // TODO: 16/03/2022 Compare against last week

        // TODO: 16/03/2022 Autofill names
        // TODO: 16/03/2022 User context command
        assert guild != null;
        guild.updateCommands()
                .addCommands(
                        Commands.slash(
                                "raiderio",
                                "Get raider.io information for a player."
                        ).addOption(
                                OptionType.STRING,
                                "name",
                                "The name of the player.",
                                true
                        )
                )
                .queue();

        /*

        /raiderio %realm% %name%

         */
    }
}
