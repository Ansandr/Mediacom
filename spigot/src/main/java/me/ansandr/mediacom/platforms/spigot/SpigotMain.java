package me.ansandr.mediacom.platforms.spigot;

import me.ansandr.mediacom.common.Mediacom;
import me.ansandr.mediacom.platforms.spigot.command.SpigotMediaCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Main class for Spigot platform
 */
public class SpigotMain extends JavaPlugin {

    private FileConfiguration config;
    private Map<String, Command> mediaCommandMap;

    @Override
    public void onEnable() {
        getLogger().info("Detected server platform: Bukkit. " + Bukkit.getBukkitVersion().split("-")[0] +
                " (" + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ")");

        saveDefaultConfig();
        reload();
        RootCommand command = new RootCommand();
        getCommand("media").setExecutor(command);
        getCommand("media").setTabCompleter(command);
    }

    public int reload() {
        config = getConfig();
        reloadConfig();
        mediaCommandMap = new HashMap<>();
        unregisterCommands();
        return registerCommands();
    }

    /**
     * Register commands
     * @return number of registered commands
     */
    private int registerCommands() {
        CommandMap map = getCommandMap();
        ConfigurationSection media = config.getConfigurationSection("media");
        int i = 0;
        for (String key : media.getKeys(false)) {
            String label = key;
            String url = media.getString(key + ".link");
            List<String> aliases = media.getStringList(key + ".aliases");
            List<String> message = media.getStringList(key + ".message");

            SpigotMediaCommand command = new SpigotMediaCommand(label, message, url, aliases);

            map.register("media", command);
            mediaCommandMap.put(label, command);
            i++;
        }
        return i;
    }

    private void unregisterCommands() {
        CommandMap map = getCommandMap();
        try {
            Field field = map.getClass().getDeclaredField("knownCommands");
            field.setAccessible(true);
            Map<String, Command> serverCommands = (Map<String, Command>) field.get(map);
            for (Command cmd : mediaCommandMap.values()) {
                serverCommands.remove(cmd);
                for (String alias : cmd.getAliases())
                    serverCommands.remove(alias);
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    //REFLECTION
    public CommandMap getCommandMap() {
        try {
            Field field = getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(Bukkit.getServer());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
/*
    //PAPER
    public CommandMap getCommandMap() {
        return Bukkit.getServer().getCommandMap();
    }
    //NMS
    public CommandMap getCommandMap() {
        CraftServer server = (CraftServer)Bukkit.getServer();
        return server.getCommandMap();
    }
*/

    /**
     * Media command for spigot
     */
    public class RootCommand implements CommandExecutor, TabExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length == 0) {
                for (String line : getConfig().getStringList("list")) {
                    sender.spigot().sendMessage(Mediacom.format(line));
                }
            }
            if (args.length == 1) {
                if (sender.hasPermission("mediacom.admin")) {
                    sender.sendMessage(ChatColor.RED + "Influence permission");
                    return true;
                }
                if (args[0].equals("reload")) {
                    int registeredCommands = reload();
                    sender.sendMessage("Config reloaded");
                    sender.sendMessage(ChatColor.YELLOW + (registeredCommands + " ") + ChatColor.RESET + "commands successful registered");
                    return true;
                }
                if (args[0].equals("list")) {
                    sender.sendMessage("Available commands:");
                    int i = 0;
                    for (String key : mediaCommandMap.keySet()) {
                        sender.sendMessage((++i) + ". " + key);
                    }
                    return true;
                }
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (args.length == 1) {
                if (sender.hasPermission("mediacom.admin"))
                    return Arrays.asList("list", "reload");
            }
            return new ArrayList<>();
        }
    }
}
