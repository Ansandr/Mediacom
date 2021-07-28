package me.ansandr.mediacom.platforms.bungeecord;

import me.ansandr.mediacom.common.Platform;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlatform implements Platform {

    private BungeeMain plugin;

    public BungeePlatform(BungeeMain plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getServerVersion() {
        return plugin.getProxy().getVersion();
    }
}
