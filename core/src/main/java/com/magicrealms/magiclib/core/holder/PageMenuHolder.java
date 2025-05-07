package com.magicrealms.magiclib.core.holder;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 翻页菜单构造器
 * @date 2024-04-11
 */
@Getter
@SuppressWarnings("unused")
public abstract class PageMenuHolder extends BaseMenuHolder {

    /* 是否开启缓存 */
    private final boolean cache;
    /* 缓存管理器 */
    private final CacheManager cacheManager;
    /* 当前页数 */
    private int page;
    /* 最大页数 */
    @Setter
    private int maxPage;

    public PageMenuHolder(MagicRealmsPlugin plugin,
                          Player player,
                          String configPath,
                          String defLayout) {
        this(plugin, player, configPath, defLayout, true, true, null);
    }

    public PageMenuHolder(MagicRealmsPlugin plugin,
                          Player player,
                          String configPath,
                          String defLayout,
                          boolean cache) {
        this(plugin, player, configPath, defLayout, true, cache, null);
    }

    public PageMenuHolder(MagicRealmsPlugin plugin,
                          Player player,
                          String configPath,
                          String defLayout,
                          @Nullable Runnable backMenuRunnable) {
        this(plugin, player, configPath, defLayout, true, true, backMenuRunnable);
    }

    public PageMenuHolder(MagicRealmsPlugin plugin,
                          Player player,
                          String configPath,
                          String defLayout,
                          boolean cache,
                          @Nullable Runnable backMenuRunnable) {
        this(plugin, player, configPath, defLayout, true, true, backMenuRunnable);
    }

    public PageMenuHolder(MagicRealmsPlugin plugin,
                          Player player,
                          String configPath,
                          String defLayout,
                          boolean lock,
                          boolean cache,
                          @Nullable Runnable backMenuRunnable) {
        super(plugin, player, configPath, defLayout, lock, backMenuRunnable);
        this.cacheManager = new CacheManager(this);
        this.cache = cache;
        this.page = 1;
        this.maxPage = 1;
    }

    protected abstract void handleMenuUnCache(String layout);

    protected abstract String handleTitleMore(String layout);

    @Override
    protected void handleMenu(String layout) {
        if (cache && cacheManager.getCache().containsKey(page) && cacheManager.getCache().get(page) != null) {
            int size = layout.length();
            for (int i = 0; i < size; i++) {
                super.setItemSlot(i, cacheManager.getCache().get(page).getOrDefault(i, ItemUtil.AIR));
            }
            return;
        }
        handleMenuUnCache(layout);
    }

    @Override
    protected String handleTitle(String title) {
        return handleTitleMore(StringUtil.replacePlaceholders(title, Map.of(
                "page", String.valueOf(page),
                "max_page", String.valueOf(maxPage)
        )));
    }

    protected boolean setCurrentPage(int page) {
        if (page >= 1 && page <= maxPage) {
            this.page = page;
            return true;
        }
        return false;
    }

    protected boolean goToFirstPage() {
        return setCurrentPage(1);
    }

    protected boolean goToLastPage() {
        return setCurrentPage(maxPage);
    }

    protected void changePage(int delta, Consumer<Boolean> callBack) {
        callBack.accept(setCurrentPage(page + delta));
    }

    @Override
    public void setItemSlot(int slot, ItemStack itemStack) {
        super.setItemSlot(slot, itemStack);
        cacheManager.cache(slot, itemStack);
    }

}
