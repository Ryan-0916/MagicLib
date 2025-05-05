package com.magicrealms.magiclib.core.holder;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.bukkit.holder.IBaseMenuHolder;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import com.magicrealms.magiclib.bukkit.utils.ItemUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.dispatcher.NMSDispatcher;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @Desc 基础菜单构造器
 * @date 2024-04-11
 */
@Data
@SuppressWarnings("unused")
public class BaseMenuHolder implements InventoryHolder, IBaseMenuHolder {

    private final MagicRealmsPlugin plugin;
    private final Player player;
    private final boolean lock;
    private final String layout;
    private final String title;
    private final String configPath;
    private final Inventory inventory;
    private final Runnable backMenuRunnable;
    private final Map<Integer, ItemStack> cooldownItems;
    private boolean playOpenSound;
    private boolean playCloseSound;

    public BaseMenuHolder(MagicRealmsPlugin plugin,
                          Player player,
                          String configPath,
                          String defLayout) {
        this(plugin, player, configPath, defLayout, true);
    }

    public BaseMenuHolder(MagicRealmsPlugin plugin,
                          Player player,
                          String configPath,
                          String defLayout,
                          boolean lock) {
        this(plugin, player, configPath, defLayout, lock, null);
    }

    public BaseMenuHolder(MagicRealmsPlugin plugin,
                          Player player,
                          String configPath,
                          String defLayout,
                          @Nullable Runnable backMenuRunnable) {
        this(plugin, player, configPath, defLayout, true, backMenuRunnable);
    }

    public BaseMenuHolder(MagicRealmsPlugin plugin, Player player,
                          String configPath, String defLayout,
                          boolean lock, @Nullable Runnable backMenuRunnable) {
        this.plugin = plugin;
        this.player = player;
        this.configPath = configPath;
        this.layout = setupLayout(defLayout);
        this.title = setupTitle();
        this.lock = lock;
        this.backMenuRunnable = backMenuRunnable;
        this.cooldownItems = new ConcurrentHashMap<>();
        this.inventory = Bukkit.createInventory(this, layout.length());
        this.playOpenSound = true;
        this.playCloseSound = true;
    }

    private String setupTitle() {
        String titleText = "Title.%s.Text";
        String titleOffset = "Title.%s.Offset";
        Optional<Set<String>> titles =  plugin.getConfigManager()
                .getYmlSubKeys(configPath, "Title", false);
        return titles.map(strings -> strings.stream()
                .map(s -> MagicLib.getInstance().getOffsetManager()
                        .format(plugin.getConfigManager().getYmlValue(configPath,
                                        String.format(titleOffset, s), 0, ParseType.INTEGER),
                                plugin.getConfigManager().getYmlValue(configPath,
                                        String.format(titleText, s), StringUtil.EMPTY, ParseType.STRING)
                        )).collect(Collectors.joining())).orElse(StringUtil.EMPTY);
    }

    private String setupLayout(String defLayout) {
        Optional<List<String>> layout
                = plugin.getConfigManager().getYmlListValue(configPath, "Layout");
        if (layout.isEmpty()) {
            return defLayout;
        }
        if (layout.get().size() > 6) {
            return defLayout;
        }
        return layout.get().stream()
                .anyMatch(row -> row.length() != 9) ? defLayout : String.join("", layout.get());
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
    public void clickSlotEvent(InventoryClickEvent e, int clickedSlot) {}

    @Override
    public void dragEvent(InventoryDragEvent e) {}

    @Override
    public void openEvent(InventoryOpenEvent e) {
        /* 播放开启 GUI 的声音
           通常来说该声音在一个周期内只会播放一次
        */
        if (playOpenSound) {
            playSound("Sound.Open");
            this.playOpenSound = false;
        }
    }

    @Override
    public void closeEvent(InventoryCloseEvent e) {
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
    public void clickBottomSlotEvent(InventoryClickEvent e, int clickedSlot) {

    }

    public void playSound(String key) {
        if (!plugin.getConfigManager().containsYmlKey(configPath, key + ".Path")) {
            return;
        }
        player.playSound(player, plugin.getConfigManager().getYmlValue(configPath, key + ".Path"),
                plugin.getConfigManager()
                        .getYmlValue(configPath, key + ".Volume", 1.0F, ParseType.FLOAT),
                plugin.getConfigManager()
                        .getYmlValue(configPath, key + ".Pitch", 1.0F, ParseType.FLOAT));
    }

    public void setItemSlot(int slot, ItemStack itemStack) {
        /* 如果冷却中不存在此缓存则不替换（原因会将冷却物品替换） */
        if (!cooldownItems.containsKey(slot)) {
            inventory.setItem(slot , itemStack);
        }
        /* 如果冷却中存在此项 */
        cooldownItems.computeIfPresent(slot, (k, y) -> itemStack);
    }

    public void setItemSlot(int slot, ItemFlag... itemFlags){
        char slotChar = layout.charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManager(),
                configPath,
                "Icons." + slotChar + ".Display"));
    }

    public void setButtonSlot(int slot, boolean disabled, ItemFlag... itemFlags){
        char slotChar = layout.charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManager(),
                configPath,
                "Icons." + slotChar + (disabled ? ".DisabledDisplay" : ".ActiveDisplay")));
    }

    public void setCheckBoxSlot(int slot, boolean opened, ItemFlag... itemFlags){
        char slotChar = layout.charAt(slot);
        this.setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManager(),
                configPath,
                "Icons." + slotChar + (opened ? ".OpenDisplay" : ".CloseDisplay")));
    }

    public Component getTitle(Map<String, String> map) {
        map.put("player_name", player.getName());
        return AdventureHelper.deserializeComponent(AdventureHelper
                .legacyToMiniMessage(StringUtil.replacePlaceholders(title, map)));
    }

    protected void updateTitle(Map<String, String> map) {
        map.put("player_name", player.getName());
        NMSDispatcher.getInstance()
                .updateInventoryTitle(player,
                        StringUtil.replacePlaceholders(title, map));
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

        String key = "Icons." + character + ".Cooldown";
        if (!plugin.getConfigManager().containsYmlKey(configPath, key)) {
            return true;
        }

        /* 如果添加失败证明正在冷却中 */
        if (cooldownItems.putIfAbsent(clickSlot, slotItem) != null) {
            return false;
        }

        /* 如果不是空气则替将它放置到槽位中 */
        ItemStack cooldownItemStack = ItemUtil.getItemStackByConfig(plugin.getConfigManager(),
                configPath,
                "Icons." + character + ".CooldownDisplay");
        if (ItemUtil.isNotAirOrNull(cooldownItemStack)) {
            inventory.setItem(clickSlot, cooldownItemStack);
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ItemStack itemStack = cooldownItems.get(clickSlot);
            cooldownItems.remove(clickSlot);
            inventory.setItem(clickSlot, itemStack);
        }, Math.round(plugin.getConfigManager().getYmlValue(configPath, key, 1.0D, ParseType.DOUBLE) * 20L));
        return true;
    }
}
