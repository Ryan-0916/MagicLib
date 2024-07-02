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
        System.out.println("进入");
        commandManager.registerCommand("MagicLib", e -> {
            System.out.println("注册成功");
            System.out.println("注册成功");
            System.out.println("注册成功");
            System.out.println("注册成功");
        });
    }

    @Override
    protected void loadConfig(ConfigManage configManage) {

    }

    public static MagicLib getInstance() { return INSTANCE; }

}
