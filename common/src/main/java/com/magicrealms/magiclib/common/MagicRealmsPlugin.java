package com.magicrealms.magiclib.common;


import com.magicrealms.magiclib.common.manage.CommandManager;
import com.magicrealms.magiclib.common.manage.ConfigManage;
import com.magicrealms.magiclib.common.manage.LoggerManage;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Ryan-0916
 * @Desc
 * @date 2024-05-26
 **/
@Getter
@SuppressWarnings("unused")
public abstract class MagicRealmsPlugin extends JavaPlugin {

    private ConfigManage configManage;

    private CommandManager commandManager;

    private LoggerManage loggerManager;

    @Override
    public void onEnable() {
        super.onEnable();
        this.setupLoggerManager();
        this.setupConfigManager();
        this.setupCommandManger();
    }

    @Override
    public void onDisable() {
    }

    private void setupConfigManager() {
        this.configManage = new ConfigManage(this);
    }

    private void setupCommandManger() {
        this.commandManager = new CommandManager(this);
    }

    private void setupLoggerManager() {
        this.loggerManager = new LoggerManage(this);
    }

    public void dependenciesCheck(Runnable runnable, String... dependenciesName) {
        for (String name : dependenciesName) {
            if (Bukkit.getPluginManager().getPlugin(name) == null) {
                loggerManager.warning("缺少必要依赖项 " + name);
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        runnable.run();
    }

    public boolean dependenciesCheck(String... dependenciesName) {
        for (String name : dependenciesName) {
            if (Bukkit.getPluginManager().getPlugin(name) == null) {
                return false;
            }
        }
        return true;
    }

    protected abstract void registerCommand(CommandManager commandManager);

    protected abstract void loadConfig(ConfigManage configManage);
}
