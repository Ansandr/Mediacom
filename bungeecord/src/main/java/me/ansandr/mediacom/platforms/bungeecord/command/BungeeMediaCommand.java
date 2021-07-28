package me.ansandr.mediacom.platforms.bungeecord.command;

import me.ansandr.mediacom.common.Mediacom;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class BungeeMediaCommand extends Command {

    private List<String> message;
    private String url;

    public BungeeMediaCommand(String name, List<String> message, String url) {
        super(name);
        this.message = message;
        this.url = url;
    }

    public BungeeMediaCommand(String name, List<String> message, String url, List<String> aliases) {
        super(name, null , aliases.toArray(new String[0]));
        this.message = message;
        this.url = url;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String line : message) {
            line = line.replace("{url}", url);
            line = line.replace("{sender}", sender.getName());
            BaseComponent[] base = Mediacom.format(line);

            sender.sendMessage(base);
        }
    }

    public String getUrl() {
        return url;
    }

    public List<String> getMessage() {
        return message;
    }
}
