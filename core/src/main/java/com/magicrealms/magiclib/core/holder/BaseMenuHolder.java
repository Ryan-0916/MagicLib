package com.magicrealms.magiclib.core.holder;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.utils.ItemUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.dispatcher.NMSDispatcher;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @date 2024-04-11
 */
@SuppressWarnings("unused")
@Getter
public abstract class BaseMenuHolder
        implements InventoryHolder, IBaseMenuHolder {

    /* 持有容器的插件 */
    private final MagicRealmsPlugin plugin;
    /* 容器配置文件地址 */
    private final String configPath;
    /* 持有者 */
    private final Player player;
    /* 容器布局 */
    private final String layout;
    /* 容器Title */
    private final String title;
    /* 容器是否上锁 */
    private final boolean lock;
    /* 返回上一页的 Task */
    private final Runnable backMenuRunnable;
    /* 冷却管理器 */
    private final CooldownManager cooldownManager;
    /* 容器 */
    private final Inventory inventory;
    /* 禁用关闭声音 */
    @Setter
    private boolean disabledCloseSound;

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
        String titleText = "Title.%s.Text";
        String titleOffset = "Title.%s.Offset";
        this.plugin = plugin;
        this.configPath = configPath;
        this.player = player;
        /* layout 部分布局 */
        this.layout = plugin
                .getConfigManager()
                .getYmlListValue(configPath, "Layout")
                .filter(l -> l.size() <= 6 && l.stream().allMatch(row -> row.length() == 9))
                .map(l -> String.join("", l))
                .orElse(defLayout);
        /* title */
        this.title = plugin.getConfigManager()
                .getYmlSubKeys(configPath, "Title", false)
                .map(strings -> strings.stream()
                        .map(s -> MagicLib.getInstance().getOffsetManager()
                                .format(plugin.getConfigManager().getYmlValue(configPath,
                                                String.format(titleOffset, s), 0, ParseType.INTEGER),
                                        plugin.getConfigManager().getYmlValue(configPath,
                                                String.format(titleText, s), StringUtil.EMPTY, ParseType.STRING)
                                )).collect(Collectors.joining())).orElse(StringUtil.EMPTY);
        this.lock = lock;
        this.backMenuRunnable = backMenuRunnable;
        this.cooldownManager = new CooldownManager(this);
        this.inventory = Bukkit.createInventory(this, layout.length());
    }


    protected abstract void handleMenu(String layout);

    protected abstract String handleTitle(String title);

    protected void asyncCloseMenu() {
        Bukkit.getScheduler().runTask(plugin, () -> player.closeInventory());
    }

    protected void asyncOpenMenu() {
        /* 发包打开菜单 */
        Bukkit.getScheduler().runTask(plugin, () -> {
            handleMenu(layout);
            NMSDispatcher.getInstance().openCustomInventory(player,
                inventory, handleTitle(title));
        });
    }

    protected void asyncUpdateTitle() {
        CompletableFuture.runAsync(() -> NMSDispatcher.getInstance()
                .updateInventoryTitle(player, handleTitle(title)));
    }

    protected void backMenu() {
        if (backMenuRunnable == null) {
            asyncCloseMenu();
            return;
        }
        this.disabledCloseSound = true;
        backMenuRunnable.run();
    }

    protected void asyncPlaySound(String path) {
        CompletableFuture.runAsync(() -> {
            String soundPath = path + ".Path";
            String volumePath = path + ".Volume";
            String pitchPath = path + ".Pitch";
            String speedPath = path + ".Speed";
            if (!plugin.getConfigManager()
                    .containsYmlKey(configPath, path + ".Path")) {
                return;
            }
           NMSDispatcher.getInstance().playSound(player,
                   plugin.getConfigManager().getYmlValue(configPath, soundPath),
                   plugin.getConfigManager().getYmlValue(configPath, volumePath, 1.0F, ParseType.FLOAT),
                   plugin.getConfigManager().getYmlValue(configPath, pitchPath, 1.0F, ParseType.FLOAT),
                   plugin.getConfigManager().getYmlValue(configPath, speedPath, 1L, ParseType.LONG)
                   );
        });
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void openEvent(InventoryOpenEvent e) {
        /* 异步播放开启声音 */
        asyncPlaySound("Sound.Open");
    }

    @Override
    public void closeEvent(InventoryCloseEvent e) {
        cooldownManager.destroy();
        if (disabledCloseSound) { return; }
        /* 异步播放关闭声音 */
        asyncPlaySound("Sound.Close");
    }

    public void setItemSlot(int slot, ItemStack itemStack) {
        /* 如果冷却中不存在此缓存则不替换（原因会将冷却物品替换） */
        if (!cooldownManager.getCooldowns().containsKey(slot)) {
            inventory.setItem(slot , itemStack);
        }
        /* 如果冷却中存在此项 */
        cooldownManager.getCooldowns().computeIfPresent(slot, (k, y) -> itemStack);
    }

    public void setItemSlot(int slot, ItemFlag... itemFlags){
        char slotChar = layout.charAt(slot);
        setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManager(),
                configPath, "Icons." + slotChar + ".Display"));
    }

    public void setButtonSlot(int slot, boolean disabled, ItemFlag... itemFlags){
        char slotChar = layout.charAt(slot);
        setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManager(),
                configPath,
                "Icons." + slotChar + (disabled ? ".DisabledDisplay" : ".ActiveDisplay")));
    }

    public void setCheckBoxSlot(int slot, boolean opened, ItemFlag... itemFlags){
        char slotChar = layout.charAt(slot);
        setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManager(),
                configPath,
               "Icons." + slotChar + (opened ? ".OpenDisplay" : ".CloseDisplay")));
    }

}
