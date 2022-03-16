package net.envirocraft;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.envirocraft.utils.Config;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.URISyntaxException;

public class Bot {

    public static JDA jda = null;
    public static String localPath = null;

    public static void main(String[] args) throws LoginException, URISyntaxException {

        File jarFile = new File(Bot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        localPath = jarFile.getParent() + File.separator;

        Config.load();

        jda = JDABuilder.createDefault(Config.get("token").getAsString())
                .build();
    }
}
