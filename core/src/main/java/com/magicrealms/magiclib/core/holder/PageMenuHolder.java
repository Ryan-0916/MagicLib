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

    private static final ItemStack AIR = ItemUtil.AIR;

    private final boolean cache;
    private final CacheManager cacheManager;
    private int page;
    @Setter private int maxPage;

    protected abstract void handleMenuUnCache(String layout);


    protected abstract LinkedHashMap<String, String> processHandTitle(LinkedHashMap<String, String> title);

    @Override
    protected final void handleMenu(String layout) {
        if (shouldUseCache()) {
            restoreCachedLayout(layout.length());
            return;
        }
        handleMenuUnCache(layout);
    }

    private boolean shouldUseCache() {
        return cache && cacheManager.hasCachedPage(page);
    }

    private void restoreCachedLayout(int layoutSize) {
        Map<Integer, ItemStack> cachedPage = cacheManager.getCachedPage(page);
        for (int slot = 0; slot < layoutSize; slot++) {
            super.setItemSlot(slot, cachedPage.getOrDefault(slot, AIR));
        }
    }

    @Override
    protected LinkedHashMap<String, String> handleTitle(LinkedHashMap<String, String> title) {
        return processHandTitle(handeTitlePlaceholders(title));
    }

    private LinkedHashMap<String, String> handeTitlePlaceholders(LinkedHashMap<String, String> title) {
        Map<String, String> placeholders = createPlaceholders();
        return title.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> StringUtil.replacePlaceholders(entry.getValue(), placeholders),
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));
    }

    private Map<String, String> createPlaceholders() {
        return Map.of(
                "page", String.valueOf(page),
                "max_page", String.valueOf(maxPage),
                "pre_page", getPageNavigationText("PrePage", page != 1),
                "next_page", getPageNavigationText("NextPage", page != maxPage)
        );
    }

    private String getPageNavigationText(String pageType, boolean enabled) {
        String path = String.format("CustomPapi.%s.%s", pageType, enabled ? "Enable" : "UnEnable");
        return super.getConfigValue(path, StringUtil.EMPTY, ParseType.STRING);
    }

    protected boolean setCurrentPage(int page) {
        if (isValidPage(page)) {
            this.page = page;
            return true;
        }
        return false;
    }

    private boolean isValidPage(int page) {
        return page >= 1 && page <= maxPage;
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
    public final void setItemSlot(int slot, ItemStack itemStack) {
        super.setItemSlot(slot, itemStack);
        if (cache) {
            cacheManager.cache(slot, itemStack);
        }
    }

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

}
