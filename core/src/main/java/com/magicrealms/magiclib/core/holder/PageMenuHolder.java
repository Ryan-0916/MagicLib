package com.magicrealms.magiclib.core.holder;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    protected abstract Map<String, String> handleTitleMore(Map<String, String> title);

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
    protected Map<String, String> handleTitle(Map<String, String> title) {
        Map<String, String> placeholder = Map.of(
                "page", String.valueOf(page),
                "max_page", String.valueOf(maxPage),
                "pre_page", (page != 1 ? super.getConfigValue("CustomPapi.PrePage.Enable", StringUtil.EMPTY, ParseType.STRING)
                        : super.getConfigValue("CustomPapi.PrePage.UnEnable", StringUtil.EMPTY, ParseType.STRING)),
                "next_page", (page != maxPage ? super.getConfigValue("CustomPapi.NextPage.Enable", StringUtil.EMPTY, ParseType.STRING)
                        : super.getConfigValue("CustomPapi.NextPage.UnEnable", StringUtil.EMPTY, ParseType.STRING))
        );
        return handleTitleMore(
                title.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> StringUtil.replacePlaceholders(entry.getValue(), placeholder),
                                (oldVal, newVal) -> oldVal,  // 合并函数
                                LinkedHashMap::new            // 保证顺序
                        ))
        );
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
