package com.magicrealms.magiclib.paper.holder;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.command.enums.YmlValueType;
import com.magicrealms.magiclib.common.holder.IBaseMenuHolder;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.ItemUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.paper.dispatcher.NMSDispatcher;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan-0916
 * @Desc 基础菜单构造器
 * @date 2024-04-11
 */
@Data
@SuppressWarnings("unused")
public class BaseMenuHolder implements InventoryHolder, IBaseMenuHolder {

    private final Player player;
    private final boolean lock;
    private final String layout;
    private final String title;
    private final MagicRealmsPlugin plugin;
    private final String configPath;
    private final Inventory inventory;
    private final Runnable backMenuRunnable;
    private final Map<Integer, ItemStack> cooldownItems;
    private boolean playOpenSound;
    private boolean playCloseSound;

    public BaseMenuHolder(@NotNull MagicRealmsPlugin plugin,
                          @NotNull Player player,
                          @NotNull String configPath,
                          @NotNull String defLayout) {
        this(plugin, player, configPath, defLayout, true);
    }

    public BaseMenuHolder(@NotNull MagicRealmsPlugin plugin,
                          @NotNull Player player,
                          @NotNull String configPath,
                          @NotNull String defLayout,
                          boolean lock) {
        this(plugin, player, configPath, defLayout, lock, null);
    }

    public BaseMenuHolder(@NotNull MagicRealmsPlugin plugin,
                          @NotNull Player player,
                          @NotNull String configPath,
                          @NotNull String defLayout,
                          @Nullable Runnable backMenuRunnable) {
        this(plugin, player, configPath, defLayout, true, backMenuRunnable);
    }

    public BaseMenuHolder(@NotNull MagicRealmsPlugin plugin, @NotNull Player player,
                          @NotNull String configPath, @NotNull String defLayout,
                          boolean lock, @Nullable Runnable backMenuRunnable) {
        this.plugin = plugin;
        this.player = player;
        this.configPath = configPath;
        this.layout = setupLayout(defLayout);
        this.title = plugin.getConfigManage().getYmlValue(configPath, "Title");
        this.lock = lock;
        this.backMenuRunnable = backMenuRunnable;
        this.cooldownItems = new ConcurrentHashMap<>();
        this.inventory = Bukkit.createInventory(this, layout.length());
        this.playOpenSound = true;
        this.playCloseSound = true;
    }

    @NotNull
    private String setupLayout(@NotNull String defLayout) {
        Optional<List<String>> layout = plugin.getConfigManage().getYmlListValue(configPath, "Layout");
        if (layout.isEmpty()) {
            return defLayout;
        }

        if (layout.get().size() > 6) {
            return defLayout;
        }

        return layout.get().stream().anyMatch(row -> row.length() != 9) ? defLayout : String.join("", layout.get());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void closeMenu() {
        Bukkit.getScheduler().runTask(plugin, () -> player.closeInventory());
    }

    @Override
    public void clickSlotEvent(@NotNull InventoryClickEvent e, int clickedSlot) {}

    @Override
    public void dragEvent(@NotNull InventoryDragEvent e) {}

    @Override
    public void openEvent(@NotNull InventoryOpenEvent e) {
        /* 播放开启 GUI 的声音
           通常来说该声音在一个周期内只会播放一次
        */
        if (playOpenSound) {
            playSound("Sound.Open");
            this.playOpenSound = false;
        }
    }

    @Override
    public void closeEvent(@NotNull InventoryCloseEvent e) {
        /* 播放关闭 GUI 的声音
           通常来说该声音在一个周期内只会播放一次
           一般实在主动关闭的情况下播放该声音
        */
        if (playCloseSound) {
            playSound("Sound.Close");
            return;
        }
        this.playCloseSound = true;
    }

    @Override
    public void clickBottomSlotEvent(@NotNull InventoryClickEvent e, int clickedSlot) {

    }

    public void playSound(@NotNull String key) {
        if (!plugin.getConfigManage().containsYmlKey(configPath, key + ".path")) {
            return;
        }
        player.playSound(player, plugin.getConfigManage().getYmlValue(configPath, key + ".path"),
                plugin.getConfigManage().getYmlValue(configPath, key + ".volume", 1.0F, YmlValueType.FLOAT),
                plugin.getConfigManage().getYmlValue(configPath, key + ".pitch", 1.0F, YmlValueType.FLOAT));
    }

    public void setItemSlot(int slot, @NotNull ItemStack itemStack) {
        /* 如果冷却中不存在此缓存则不替换（原因会将冷却物品替换） */
        if (!cooldownItems.containsKey(slot)) {
            inventory.setItem(slot , itemStack);
        }
        /* 如果冷却中存在此项 */
        cooldownItems.computeIfPresent(slot, (k, y) -> itemStack);
    }

    public void setItemSlot(int slot, ItemFlag... itemFlags){
        char slotChar = layout.charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManage(),
                configPath,
                "Icons." + slotChar + ".display"));
    }

    public void setButtonSlot(int slot, boolean disabled, ItemFlag... itemFlags){
        char slotChar = layout.charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManage(),
                configPath,
                "Icons." + slotChar + (disabled ? ".disabledDisplay" : ".activeDisplay")));
    }

    public void setCheckBoxSlot(int slot, boolean opened, ItemFlag... itemFlags){
        char slotChar = layout.charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManage(),
                configPath,
                "Icons." + slotChar + (opened ? ".openDisplay" : ".closeDisplay")));
    }

















    @NotNull
    public Component getTitle(@NotNull Map<String, String> map) {
        map.put("player_name", player.getName());
        return AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(StringUtil.replacePlaceholder(title, map)));
    }

    protected void updateTitle(@NotNull Map<String, String> map) {
        NMSDispatcher.getInstance().updateInventoryTitle(player, title);
    }

    public boolean hasBackMenuRunnable() {
        return backMenuRunnable != null;
    }

    public void backMenuRun() {
        if (backMenuRunnable != null) {
            this.playCloseSound = false;
            backMenuRunnable.run();
            return;
        }
        closeMenu();
    }

    /**
     * 尝试给某个槽点加冷却
     * 如果添加成功即代表此槽点暂无冷却
     * 如果添加失败即代表此槽点正在冷却中
     * @param clickSlot 点击槽点
     * @return 返回是否添加冷却成功 {@link Boolean}
     */
    public boolean tryCooldown(int clickSlot) {
        if (clickSlot < 0 || clickSlot >= layout.length()) {
            return true;
        }

        char character = layout.charAt(clickSlot);
        ItemStack slotItem = inventory.getItem(clickSlot);
        if (slotItem == null) {
            return true;
        }

        String key = "Icons." + character + ".cooldown";
        if (!plugin.getConfigManage().containsYmlKey(configPath, key)) {
            return true;
        }

        /* 如果添加失败证明正在冷却中 */
        if (cooldownItems.putIfAbsent(clickSlot, slotItem) != null) {
            return false;
        }

        /* 如果不是空气则替将它放置到槽位中 */
        ItemStack cooldownItemStack = ItemUtil.getItemStackByConfig(plugin.getConfigManage(),
                configPath,
                "Icons." + character + ".cooldownDisplay");
        if (ItemUtil.isNotAirOrNull(cooldownItemStack)) {
            inventory.setItem(clickSlot, cooldownItemStack);
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ItemStack itemStack = cooldownItems.get(clickSlot);
            cooldownItems.remove(clickSlot);
            inventory.setItem(clickSlot, itemStack);
        }, Math.round(plugin.getConfigManage().getYmlValue(configPath, key, 1.0D, YmlValueType.DOUBLE) * 20L));
        return true;
    }
}
