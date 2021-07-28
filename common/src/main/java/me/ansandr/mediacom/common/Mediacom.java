package me.ansandr.mediacom.common;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.logging.Logger;

public class Mediacom {

    private static Mediacom instance;
    private static Logger LOGGER;

    private Platform platform;

    public Mediacom(Platform platform) {
        this.platform = platform;
    }

    public Platform getPlatform() {
        return platform;
    }

    public static BaseComponent[] format(String json) {
        return ComponentSerializer.parse(json);
    }

    public static Mediacom getInstance() {
        return instance;
    }
}
