package com.magicrealms.magiclib.core.manager;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc 经济 API
 * @date 2025-06-13
 */
@SuppressWarnings("unused")
public interface IVaultManager {

    /**
     * 余额是否足够多少数量
     * @param player 玩家
     * @param amount 余额
     * @return 玩家余额是否足够
     */
    boolean sufficientAmount(Player player, BigDecimal amount);

    /**
     * 获取玩家余额总数量
     * @param player 玩家
     * @return 玩家余额总数 {@link BigDecimal}
     */
    BigDecimal getAmount(Player player);

    /**
     * 减少玩家余额
     * @param player 玩家
     * @param amount 金额
     * @return 余额是否减少成功
     */
    boolean withdrawAmount(Player player, BigDecimal amount);

    /**
     * 增加玩家余额
     * @param player 玩家
     * @param amount 增加金额数量
     * @return 余额是否增加成功
     */
    boolean depositAmount(Player player, BigDecimal amount);

    /**
     * 增加玩家余额
     * @param playerUniqueId 玩家编号
     * @param amount 增加金额数量
     * @return 余额是否增加成功
     */
    boolean depositAmount(UUID playerUniqueId, BigDecimal amount);
}
