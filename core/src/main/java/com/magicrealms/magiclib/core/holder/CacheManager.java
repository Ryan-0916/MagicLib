package com.magicrealms.magiclib.core.holder;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-05-05
 */
@SuppressWarnings("unused")
public class CacheManager {

    private final PageMenuHolder holder;

    @Getter
    private final Map<Integer, Map<Integer, ItemStack>> cache = new ConcurrentHashMap<>();

    public CacheManager(PageMenuHolder holder) {
        this.holder = holder;
    }

    public void cache(int slot, ItemStack itemStack) {
        if (!holder.isCache()) {
            return;
        }
        Map<Integer, ItemStack> pageCache = cache
                .getOrDefault(holder.getPage(), new HashMap<>());
        pageCache.put(slot, itemStack);
        cache.put(holder.getPage(), pageCache);
    }

    public void destroy() {
        cache.clear();
    }

}
