package me.ansandr.mediacom.platforms.spigot;

import me.ansandr.mediacom.common.Platform;
import org.bukkit.plugin.Plugin;

public class SpigotPlatform implements Platform {

    private SpigotMain plugin;

    public SpigotPlatform(SpigotMain plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getServerVersion() {
        return plugin.getServer().getVersion();
    }
}
