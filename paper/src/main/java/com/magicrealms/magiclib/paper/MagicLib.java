package com.magicrealms.magiclib.paper;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.manage.CommandManager;
import com.magicrealms.magiclib.common.manage.ConfigManage;
import com.magicrealms.magiclib.paper.listener.PlayerListener;
import org.bukkit.Bukkit;

/**
 * @author Ryan-0916
 * @Desc MagicLib 启动类
 * @date 2024-05-06
 **/
public class MagicLib extends MagicRealmsPlugin {

    private static MagicLib INSTANCE;

    public static final String YML_CONFIRM_MENU = "confirmMenu";

    @Override
    public void onEnable() {
        super.onEnable();
        INSTANCE = this;
        dependenciesCheck(() -> {
            loadConfig(getConfigManage());
            registerCommand(getCommandManager());
            Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    protected void registerCommand(CommandManager commandManager) {
        commandManager.registerCommand("MagicLib", e -> {

        });
    }

    @Override
    protected void loadConfig(ConfigManage configManage) {
        configManage.loadConfig(YML_CONFIRM_MENU);
    }

    public static MagicLib getInstance() { return INSTANCE; }

}
