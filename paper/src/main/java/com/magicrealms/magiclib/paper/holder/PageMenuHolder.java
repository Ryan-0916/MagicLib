package com.magicrealms.magiclib.paper.holder;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.utils.ItemUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 翻页菜单构造器
 * @date 2024-04-11
 */
@SuppressWarnings("unused")
public class PageMenuHolder extends BaseMenuHolder {

    private final Map<Integer, Map<Integer, ItemStack>> cacheItems;
    private final boolean cache;
    @Getter
    private int page;
    @Getter @Setter
    private int maxPage;

    public PageMenuHolder(@NotNull MagicRealmsPlugin plugin,
                          @NotNull Player player,
                          @NotNull String configPath,
                          @NotNull String defLayout) {
        this(plugin, player, configPath,
                defLayout, true, true, null);
    }

    public PageMenuHolder(@NotNull MagicRealmsPlugin plugin,
                          @NotNull Player player,
                          @NotNull String configPath,
                          @NotNull String defLayout,
                          boolean cache) {
        this(plugin, player, configPath, defLayout, true, cache, null);
    }

    public PageMenuHolder(@NotNull MagicRealmsPlugin plugin,
                          @NotNull Player player,
                          @NotNull String configPath,
                          @NotNull String defLayout,
                          @Nullable Runnable backMenuRunnable) {
        this(plugin, player, configPath, defLayout, true, true, backMenuRunnable);
    }

    public PageMenuHolder(@NotNull MagicRealmsPlugin plugin,
                          @NotNull Player player,
                          @NotNull String configPath,
                          @NotNull String defLayout,
                          boolean cache,
                          @Nullable Runnable backMenuRunnable) {
        this(plugin, player, configPath, defLayout, true, true, backMenuRunnable);
    }

    public PageMenuHolder(@NotNull MagicRealmsPlugin plugin,
                          @NotNull Player player,
                          @NotNull String configPath,
                          @NotNull String defLayout,
                          boolean lock,
                          boolean cache,
                          @Nullable Runnable backMenuRunnable) {
        super(plugin, player, configPath, defLayout, lock, backMenuRunnable);
        this.cacheItems = new HashMap<>();
        this.cache = cache;
        this.page = 1;
        this.maxPage = 1;
    }

    /**
     * 加载菜单当前页的物品
     * 此方法是通过缓存去加载当前页的物品
     * 如果您不希望您的物品通过缓存的形式添加请不要使用此方法
     * @param consumer 加载成功/加载失败 {@link Consumer}
     */
    public void loadingPageItems(@NotNull Consumer<Boolean> consumer) {
        if (cache && cacheItems.containsKey(page) && cacheItems.get(page) != null) {
            int size = super.getLayout().length();
            for (int i = 0; i < size; i++) {
                super.setItemSlot(i, cacheItems.get(page).getOrDefault(i, ItemUtil.AIR));
            }
            consumer.accept(true);
            return;
        }
        consumer.accept(false);
    }

    public boolean setCurrentPage(int page) {
        if (page >= 1 && page <= maxPage) {
            super.setPlayCloseSound(false);
            this.page = page;
            return true;
        }
        return false;
    }

    public boolean goToFirstPage() {
        return setCurrentPage(1);
    }

    public boolean goToLastPage() {
        return setCurrentPage(maxPage);
    }

    public void changePage(int delta, @NotNull Consumer<Boolean> callBack) {
        callBack.accept(setCurrentPage(page + delta));
    }

    private void cacheItemStack(int slot, ItemStack itemStack) {
        if (cache) {
            Map<Integer, ItemStack> pageCache = cacheItems.getOrDefault(page, new HashMap<>());
            pageCache.put(slot, itemStack);
            cacheItems.put(page, pageCache);
        }
    }

    public void cleanItemCache() {
        cacheItems.clear();
    }

    @Override
    public void setItemSlot(int slot, @NotNull ItemStack itemStack) {
        super.setItemSlot(slot, itemStack);
        cacheItemStack(slot, itemStack);
    }

    @Override
    public void setItemSlot(int slot, ItemFlag... itemFlags){
        char slotChar = super.getLayout().charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(super.getPlugin().getConfigManage(),
                super.getConfigPath(),
                "Icons." + slotChar + ".display"));
    }

    @Override
    public void setButtonSlot(int slot, boolean disabled, ItemFlag... itemFlags){
        char slotChar =  super.getLayout().charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(super.getPlugin().getConfigManage(),
                super.getConfigPath(),
                "Icons." + slotChar + (disabled ? ".disabledDisplay" : ".activeDisplay")));
    }

    @Override
    public void setCheckBoxSlot(int slot, boolean opened, ItemFlag... itemFlags){
        char slotChar =  super.getLayout().charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(super.getPlugin().getConfigManage(),
                super.getConfigPath(),
                "Icons." + slotChar + (opened ? ".openDisplay" : ".closeDisplay")));
    }

    @Override
    public @NotNull Component getTitle(@NotNull Map<String, String> placeholderMap) {
        placeholderMap.put("page", String.valueOf(page));
        placeholderMap.put("max_page", String.valueOf(maxPage));
        placeholderMap.put("next_page_title", super.getPlugin().getConfigManage()
                .getYmlValue(super.getConfigPath(), page >= maxPage ? "PageTitle.NextPage.Disable" :
                        "PageTitle.NextPage.Active"));
        placeholderMap.put("previous_page_title", super.getPlugin().getConfigManage()
                .getYmlValue(super.getConfigPath(), page <= 1 ? "PageTitle.PreviousPage.Disable" :
                        "PageTitle.PreviousPage.Active"));
        return super.getTitle(placeholderMap);
    }
}
