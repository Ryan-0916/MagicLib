package com.magicrealms.magiclib.velocity;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author Ryan-0916
 * @Desc Velocity-MagicLib
 * @date 2025-04-29
 */
@Plugin(id = "magiclib",
        name = "MagicLib", version = "1.0",
        description = "魔法领域-集成", authors = {"Ryan0916"})
@SuppressWarnings("unused")
public class MagicLib {

    @Getter
    private static MagicLib INSTANCE;
    @Inject @Getter
    private Logger logger;
    @Getter
    private final ProxyServer server;

    @Inject
    public MagicLib(ProxyServer server, Logger logger) {
        INSTANCE = this;
        this.server = server;
        this.logger = logger;
    }

}
