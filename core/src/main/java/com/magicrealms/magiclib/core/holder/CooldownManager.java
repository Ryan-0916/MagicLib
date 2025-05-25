package com.magicrealms.magiclib.core.holder;

import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.core.dispatcher.NMSDispatcher;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan-0916
 * @Desc 容器物品冷却管理器
 * @date 2025-05-05
 */
public class CooldownManager {

    private final BaseMenuHolder holder;

    private final Map<Integer, BukkitTask> tasks = new HashMap<>();

    @Getter
    private final Map<Integer, ItemStack> cooldowns = new ConcurrentHashMap<>();

    public CooldownManager(BaseMenuHolder holder) {
        this.holder = holder;
    }

    /**
     * 尝试给某个槽点加冷却
     * 如果添加成功即代表此槽点暂无冷却
     * 如果添加失败即代表此槽点正在冷却中
     * @param clickSlot 点击槽点
     * @return 返回是否添加冷却成功 {@link Boolean}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean tryCooldown(int clickSlot) {
        /* 如果点击槽点无效，提前返回 */
        if (isInvalidSlot(clickSlot)) {
            return true;
        }
        char character = holder.getLayout().charAt(clickSlot);
        ItemStack slotItem = holder.getInventory().getItem(clickSlot);
        /* 如果槽位没有物品，或者没有配置冷却项，提前返回 */
        if (ItemUtil.isAirOrNull(slotItem) || !hasCooldownConfig(character)) {
            return true;
        }
        /* 如果槽点已经在冷却中，返回失败 */
        if (!addCooldown(clickSlot, slotItem)) {
            return false;
        }
        /* 获取冷却显示物品并设置到槽位 */
        setCooldownDisplay(clickSlot, character);
        scheduleCooldownRestore(clickSlot);
        return true;
    }

    public void destroy() {
        tasks.forEach((slot, task) -> {
            NMSDispatcher.getInstance().setItemCooldown(holder.getPlayer(),
                    holder.getInventory().getItem(slot), 0);
            if (!task.isCancelled()) {
                task.cancel();
            }
        });
    }

    private boolean isInvalidSlot(int clickSlot) {
        return clickSlot < 0 || clickSlot >= holder.getLayout().length();
    }

    private boolean hasCooldownConfig(char character) {
        String key = "Icons." + character + ".Cooldown";
        return holder.getPlugin().getConfigManager().containsYmlKey(holder.getConfigPath(), key);
    }

    private boolean addCooldown(int clickSlot, ItemStack slotItem) {
        return cooldowns.putIfAbsent(clickSlot, slotItem) == null;
    }

    private void setCooldownDisplay(int clickSlot, char character) {
        ItemStack cooldownItem = ItemUtil.getItemStackByConfig(holder.getPlugin().getConfigManager(),
                holder.getConfigPath(), "Icons." + character + ".CooldownDisplay");
        if (ItemUtil.isNotAirOrNull(cooldownItem)) {
            holder.getInventory().setItem(clickSlot, cooldownItem);
        }
    }

    private void scheduleCooldownRestore(int clickSlot) {
        String key = "Icons." + holder.getLayout().charAt(clickSlot) + ".Cooldown";
        int cooldownTime = holder.getPlugin().getConfigManager()
                .getYmlValue(holder.getConfigPath(), key, 20, ParseType.INTEGER);

        /* 添加冷却 */
        NMSDispatcher.getInstance().setItemCooldown(holder.getPlayer(),
                holder.getInventory().getItem(clickSlot), cooldownTime);

        tasks.put(clickSlot, Bukkit.getScheduler().runTaskLater(holder.getPlugin(), () -> {
            /* 移除冷却 */
            NMSDispatcher.getInstance().setItemCooldown(holder.getPlayer(),
                    holder.getInventory().getItem(clickSlot), 0);

            ItemStack originalItem = cooldowns.get(clickSlot);
            cooldowns.remove(clickSlot);
            holder.getInventory().setItem(clickSlot, originalItem);
        }, cooldownTime));
    }
}
