package com.magicrealms.magiclib.core;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.bukkit.manage.CommandManager;
import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.bukkit.manage.PacketManager;
import com.magicrealms.magiclib.core.advance.AdvanceManager;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magiclib.core.listener.PlayerListener;
import com.magicrealms.magiclib.core.manager.IVaultManager;
import com.magicrealms.magiclib.core.manager.VaultManager;
import com.magicrealms.magiclib.core.offset.OffsetManager;
import lombok.Getter;
import org.bukkit.Bukkit;

import static com.magicrealms.magiclib.core.MagicLibConstant.*;

/**
 * @author Ryan-0916
 * @Desc MagicLib 启动类
 * @date 2024-05-06
 */
@Getter
public class MagicLib extends MagicRealmsPlugin {

    private static MagicLib INSTANCE;

    private OffsetManager offsetManager;

    private AdvanceManager advanceManager;

    private IVaultManager vaultManager;

    @Override
    public void onEnable() {
        super.onEnable();
        INSTANCE = this;
        dependenciesCheck(() -> {
            loadConfig(getConfigManager());
            registerCommand(commandManager);
            registerPacketListener(packetManager);
            setupOffset();
            setupAdvance();
            setupVault();
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        }, "Vault", "packetevents");
    }

    private void setupVault() {
        this.vaultManager = new VaultManager(this);
    }

    public void setupAdvance() {
        this.advanceManager = new AdvanceManager(this);
    }

    public void setupOffset() {
        this.offsetManager = new OffsetManager(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        advanceManager.clearCache();
    }

    @Override
    protected void loadConfig(ConfigManager configManager) {
        configManager.loadConfig(YML_LANGUAGE,
                YML_CONFIRM_MENU,
                YML_OFFSET,
                YML_ADVANCE);
    }

    @Override
    protected void registerCommand(CommandManager commandManager) {
        commandManager.registerCommand(PLUGIN_NAME, e -> {
            switch (e.cause()) {
                case NOT_PLAYER -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "ConsoleMessage.Error.NotPlayer"));
                case NOT_CONSOLE -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.NotConsole"));
                case UN_KNOWN_COMMAND -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.UnknownCommand"));
                case PERMISSION_DENIED -> MessageDispatcher.getInstance().
                        sendMessage(this, e.sender(), getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.PermissionDenied"));
            }
        });
    }

    @Override
    protected void registerPacketListener(PacketManager packetManager) {
        packetManager.registerListeners();
    }

    public static MagicLib getInstance() { return INSTANCE; }

}
