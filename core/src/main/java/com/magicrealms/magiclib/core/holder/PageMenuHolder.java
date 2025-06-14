package com.magicrealms.magiclib.core.holder;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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
        Map<String, String> map = createPlaceholders();
        return title.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> StringUtil.replacePlaceholders(entry.getValue(), map),
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));
    }

    private Map<String, String> createPlaceholders() {
        return Map.of(
                "page", String.valueOf(page),
                "max_page", String.valueOf(maxPage),
                "pre_page", getCustomPapiText("PrePage", page != 1),
                "next_page", getCustomPapiText("NextPage", page != maxPage)
        );
    }

    private void cacheItemStack(int slot, ItemStack itemStack) {
        if (cache) {
            Map<Integer, ItemStack> pageCache = cacheManager.getCache()
                    .getOrDefault(page, new HashMap<>());
            pageCache.put(slot, itemStack);
            cacheManager.getCache().put(page, pageCache);
        }
    }

    public void cleanItemCache() {
        cacheManager.destroy();
    }

    @Override
    public void setItemSlot(int slot, ItemStack itemStack) {
        super.setItemSlot(slot, itemStack);
        cacheItemStack(slot, itemStack);
    }

    @Override
    public void setItemSlot(int slot, ItemFlag... itemFlags){
        char slotChar = getLayout().charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(getPlugin().getConfigManager(),
                getConfigPath(),
                "Icons." + slotChar + ".Display"));
    }

    @Override
    public void setButtonSlot(int slot, boolean disabled, ItemFlag... itemFlags){
        char slotChar =  getLayout().charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(getPlugin().getConfigManager(),
                getConfigPath(),
                "Icons." + slotChar + (disabled ? ".DisabledDisplay" : ".ActiveDisplay")));
    }

    @Override
    public void setCheckBoxSlot(int slot, boolean opened, ItemFlag... itemFlags){
        char slotChar =  getLayout().charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(getPlugin().getConfigManager(),
                getConfigPath(),
                "Icons." + slotChar + (opened ? ".OpenDisplay" : ".CloseDisplay")));
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
