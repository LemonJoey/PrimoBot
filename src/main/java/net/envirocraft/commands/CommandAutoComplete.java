package net.envirocraft.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.envirocraft.utils.API;
import net.envirocraft.utils.StringUtil;

import org.jetbrains.annotations.NonNls;

import java.util.LinkedList;
import java.util.List;

public class CommandAutoComplete extends ListenerAdapter {

    @Override
    public void onCommandAutoCompleteInteraction(@NonNls CommandAutoCompleteInteractionEvent event) {
        if (!event.getName().equals("raiderio")) return;
        List<Command.Choice> choices = new LinkedList<>();
        for (String member : API.getGuildMembers()) {
            if (choices.size() >= 25) break;
            if (StringUtil.stripAccents(member).toLowerCase().startsWith(StringUtil.stripAccents(event.getFocusedOption().getValue()).toLowerCase())) {
                choices.add(new Command.Choice(
                        member,
                        member
                ));
            }
        }
        event.replyChoices(choices).queue();
    }
}
