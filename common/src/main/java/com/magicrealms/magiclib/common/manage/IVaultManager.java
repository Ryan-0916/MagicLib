package com.magicrealms.magiclib.common.manage;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 经济管理
 * @date 2024-07-23
 */
public interface IVaultManager {

    /**
     * 余额是否足够多少数量
     * @param player 玩家
     * @param amount 余额
     * @return 玩家余额是否足够
     */
    boolean sufficientAmount(@NotNull Player player, BigDecimal amount);

    /**
     * 获取玩家余额总数量
     * @param player 玩家
     * @return 玩家余额总数 {@link BigDecimal}
     */
    BigDecimal getAmount(@NotNull Player player);

    /**
     * 减少玩家余额
     * @param player 玩家
     * @param amount 金额
     * @param callBack 回调
     */
    void withdrawAmount(@NotNull Player player, BigDecimal amount, @NotNull Consumer<Boolean> callBack);

    /**
     * 增加玩家余额
     * @param player 玩家
     * @param amount 增加金额数量
     * @param callBack 回调
     */
    void depositAmount(@NotNull Player player, BigDecimal amount, @NotNull Consumer<Boolean> callBack);

    /**
     * 增加玩家余额
     * @param playerUniqueId 玩家编号
     * @param amount 增加金额数量
     * @param callBack 回调
     */
    void depositAmount(@NotNull UUID playerUniqueId, BigDecimal amount, @NotNull Consumer<Boolean> callBack);

}
