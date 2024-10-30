package com.magicrealms.magiclib.paper;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.manage.CommandManager;
import com.magicrealms.magiclib.common.manage.ConfigManage;
import com.magicrealms.magiclib.paper.dispatcher.MessageDispatcher;
import com.magicrealms.magiclib.paper.listener.PlayerListener;
import org.bukkit.Bukkit;

/**
 * @author Ryan-0916
 * @Desc MagicLib 启动类
 * @date 2024-05-06
 */
public class MagicLib extends MagicRealmsPlugin {

    private static MagicLib INSTANCE;

    public static final String YML_CONFIRM_MENU = "menu/confirm";

    @Override
    public void onEnable() {
        super.onEnable();
        INSTANCE = this;
        dependenciesCheck(() -> {
            loadConfig(getConfigManage());
            registerCommand(getCommandManager());
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    protected void loadConfig(ConfigManage configManage) {
        configManage.loadConfig(YML_CONFIRM_MENU);
    }

    @Override
    protected void registerCommand(CommandManager commandManager) {
        commandManager.registerCommand("MagicLib", e -> {
            switch (e.cause()) {
                case NOT_PLAYER -> MessageDispatcher.getInstance().sendMessage(this, e.sender(), "您必须是玩家");
                case NOT_CONSOLE -> MessageDispatcher.getInstance().sendMessage(this, e.sender(), "您必须是控制台");
                case UN_KNOWN_COMMAND -> MessageDispatcher.getInstance().sendMessage(this, e.sender(), "未知指令");
                case PERMISSION_DENIED -> MessageDispatcher.getInstance().sendMessage(this, e.sender(), "权限不足");
            }
        });
    }

    public static MagicLib getInstance() { return INSTANCE; }

}
