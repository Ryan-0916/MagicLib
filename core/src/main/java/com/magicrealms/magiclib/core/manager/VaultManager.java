package com.magicrealms.magiclib.core.manager;

import com.magicrealms.magiclib.core.MagicLib;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

/**
 * @author Ryan-0916
 * @Desc 经济 API
 * @date 2025-06-13
 */
public class VaultManager implements IVaultManager {

    private final MagicLib PLUGIN;

    public VaultManager(MagicLib plugin) {
        this.PLUGIN = plugin;
    }

    private Economy getEconomyAPI() {
        Optional<RegisteredServiceProvider<Economy>> economy = Optional.ofNullable(getServer().getServicesManager().getRegistration(Economy.class));
        if (economy.isPresent()) {
            return economy.get().getProvider();
        }
        throw new RuntimeException("服务器未找能到依赖插件 Economy");
    }

    @Override
    public boolean sufficientAmount(Player player, BigDecimal amount) {
        try {
            return getEconomyAPI().has(player, amount.doubleValue());
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("查询玩家余额是否足够时出现未知异常", e);
            return false;
        }
    }

    @Override
    public BigDecimal getAmount(Player player) {
        try {
            return BigDecimal.valueOf(getEconomyAPI().getBalance(player));
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("查询玩家总余额时出现未知异常", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public boolean withdrawAmount(Player player, BigDecimal amount) {
        try {
            return getEconomyAPI().withdrawPlayer(player, amount.doubleValue()).transactionSuccess();
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("扣除玩家余额时出现未知异常", e);
            return false;
        }
    }

    @Override
    public boolean depositAmount(Player player, BigDecimal amount) {
        try {
            return getEconomyAPI().depositPlayer(player, amount.doubleValue()).transactionSuccess();
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("增加玩家余额时出现未知异常", e);
            return false;
        }
    }

    @Override
    public boolean depositAmount(UUID playerUniqueId, BigDecimal amount) {
        try {
            return getEconomyAPI().depositPlayer(Bukkit.getOfflinePlayer(playerUniqueId), amount.doubleValue()).transactionSuccess();
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("增加玩家余额时出现未知异常", e);
            return false;
        }
    }

}
