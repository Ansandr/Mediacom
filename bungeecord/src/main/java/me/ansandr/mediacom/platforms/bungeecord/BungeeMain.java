package me.ansandr.mediacom.platforms.bungeecord;

import me.ansandr.mediacom.common.Mediacom;
import me.ansandr.mediacom.platforms.bungeecord.command.BungeeMediaCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * Main class for BungeeCord platform
 */
public final class BungeeMain extends Plugin {

    private Configuration config;
    private PluginManager pm;
    private Map<String, Command> mediaCommandMap;

    @Override
    public void onEnable() {
        getLogger().info("Detected server platform: BungeeCord. " + getProxy().getVersion());

        pm = getProxy().getPluginManager();

        saveDefaultConfig();
        reload();
        pm.registerCommand(this, new BRootCommand("media"));
    }//TODO интерфейс и API

    public int reload() {
        config = getConfig();
        mediaCommandMap = new HashMap<>();
        unregisterCommands();
        return registerCommands();
    }

    /**
     * Register commands
     * @return number of registered commands
     */
    private int registerCommands() {
        Configuration media = config.getSection("media");
        int i = 0;
        for (String key : media.getKeys()) {
            String label = key;
            String url = media.getString(key + ".link");
            List<String> aliases = media.getStringList(key + ".aliases");
            List<String> message = media.getStringList(key + ".message");

            BungeeMediaCommand command = new BungeeMediaCommand(label, message, url, aliases);

            pm.registerCommand(this, command);
            mediaCommandMap.put(label, command);
            i++;

        }
        return i;
    }

    private void unregisterCommands() {
        pm.unregisterCommands(this);
        mediaCommandMap.clear();
    }

    private Configuration getConfig() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            ex.printStackTrace();
            saveDefaultConfig();//Самый стремный костыль всех времен и народов
            return null;
        }
    }

    private void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Media command for bungeecord
     */
    public class BRootCommand extends Command implements TabExecutor {

        public BRootCommand(String name) {
            super(name, "mediacom.admin");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length == 0) {
                for (String line : getConfig().getStringList("list")) {
                    sender.sendMessage(Mediacom.format(line));
                }
            }
            if (args.length == 1) {
                if (!hasPermission(sender)) {
                    sender.sendMessage(new ComponentBuilder("Influence permission").color(ChatColor.RED).create());
                    return;
                }
                if (args[0].equals("reload")) {
                    int registeredCommands = reload();
                    sender.sendMessage(new TextComponent("Config reloaded"));
                    sender.sendMessage(new ComponentBuilder(registeredCommands + " ").color(ChatColor.YELLOW).append("commands successful registered").create());
                    return;
                }
                if (args[0].equals("list")) {
                    sender.sendMessage(new TextComponent("Available commands:"));
                    int i = 0;
                    for (String key : mediaCommandMap.keySet()) {
                        sender.sendMessage(new TextComponent((++i) + ". " + key));
                    }
                    return;
                }
            }
        }

        @Override
        public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                if (hasPermission(sender))
                    return Arrays.asList("list", "reload");
            }
            return new ArrayList<>();
        }
    }
}