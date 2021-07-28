package me.ansandr.mediacom.platforms.spigot.command;

import me.ansandr.mediacom.common.Mediacom;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SpigotMediaCommand extends Command {

    private List<String> message;
    private String url;

    public SpigotMediaCommand(String label, List<String> message, String url) {
        super(label);
        this.message = message;
        this.url = url;
    }
    public SpigotMediaCommand(String label, List<String> message, String url, List<String> aliases) {
        super(label, "", "/<command>", aliases);
        this.message = message;
        this.url = url;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        for (String line : message) {
            line = line.replace("{url}", url);
            line = line.replace("{sender}", sender.getName());
            BaseComponent[] base = Mediacom.format(line);

            sender.spigot().sendMessage(base);
        }

        return false;
    }
}
