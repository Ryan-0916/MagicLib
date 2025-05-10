package com.magicrealms.magiclib.core.holder;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.core.advance.AdvanceManager;
import com.magicrealms.magiclib.core.offset.OffsetManager;
import com.magicrealms.magiclib.core.utils.ItemUtil;
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

import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @date 2024-04-11
 */
@SuppressWarnings("unused")
@Getter
public abstract class BaseMenuHolder implements InventoryHolder, IBaseMenuHolder {
    // 配置路径常量
    private static final String TITLE_TEXT_PATH = "Title.%s.Text";
    private static final String OFFSET_PATH = "Title.%s.Offset";
    private static final String CENTER_PATH = "Title.%s.Center";
    private static final String LEFT_PATH = "Title.%s.Left";
    private static final String SOUND_PATH_SUFFIX = ".Path";
    private static final String VOLUME_PATH_SUFFIX = ".Volume";
    private static final String PITCH_PATH_SUFFIX = ".Pitch";
    private static final String SPEED_PATH_SUFFIX = ".Speed";

    private final MagicRealmsPlugin plugin;
    private final String configPath;
    private final Player player;
    private final String layout;
    private final LinkedHashMap<String, String> title;
    private final boolean lock;
    private final Runnable backMenuRunnable;
    private final CooldownManager cooldownManager;
    private final Inventory inventory;

    @Setter
    private boolean disabledCloseSound;

    // 构造方法链
    public BaseMenuHolder(MagicRealmsPlugin plugin, Player player,
                          String configPath, String defLayout) {
        this(plugin, player, configPath, defLayout, true);
    }

    public BaseMenuHolder(MagicRealmsPlugin plugin, Player player,
                          String configPath, String defLayout, boolean lock) {
        this(plugin, player, configPath, defLayout, lock, null);
    }

    public BaseMenuHolder(MagicRealmsPlugin plugin, Player player,
                          String configPath, String defLayout,
                          @Nullable Runnable backMenuRunnable) {
        this(plugin, player, configPath, defLayout, true, backMenuRunnable);
    }

    public BaseMenuHolder(MagicRealmsPlugin plugin, Player player,
                          String configPath, String defLayout,
                          boolean lock, @Nullable Runnable backMenuRunnable) {
        this.plugin = plugin;
        this.configPath = configPath;
        this.player = player;
        this.layout = initLayout(defLayout);
        this.title = initTitle();
        this.lock = lock;
        this.backMenuRunnable = backMenuRunnable;
        this.cooldownManager = new CooldownManager(this);
        this.inventory = Bukkit.createInventory(this, layout.length());
    }

    // 初始化布局
    private String initLayout(String defLayout) {
        return plugin.getConfigManager()
                .getYmlListValue(configPath, "Layout")
                .filter(l -> l.size() <= 6 && l.stream().allMatch(row -> row.length() == 9))
                .map(l -> String.join("", l))
                .orElse(defLayout);
    }

    // 初始化标题
    private LinkedHashMap<String, String> initTitle() {
        return plugin.getConfigManager()
                .getYmlSubKeys(configPath, "Title", false)
                .map(keys -> keys.stream()
                        .collect(Collectors.toMap(
                                key -> key,
                                key -> plugin.getConfigManager()
                                        .getYmlValue(configPath, String.format(TITLE_TEXT_PATH, key)),
                                (oldVal, newVal) -> oldVal,  // 合并函数（避免重复键冲突）
                                LinkedHashMap::new           // 指定使用 LinkedHashMap
                        )))
                .orElseGet(LinkedHashMap::new);  // 默认也用 LinkedHashMap
    }

    // 抽象方法
    protected abstract void handleMenu(String layout);

    protected abstract LinkedHashMap<String, String> handleTitle(LinkedHashMap<String, String> title);

    // 异步操作方法
    protected void asyncCloseMenu() {
        Bukkit.getScheduler().runTask(plugin, () -> player.closeInventory());
    }

    protected void asyncOpenMenu() {
        String formattedTitle = buildFormattedTitle();
        Bukkit.getScheduler().runTask(plugin, () -> {
            handleMenu(layout);
            NMSDispatcher.getInstance().openCustomInventory(player, inventory, formattedTitle);
        });
    }

    protected void asyncUpdateTitle() {
        CompletableFuture.runAsync(() -> {
            String formattedTitle = buildFormattedTitle();
            NMSDispatcher.getInstance().updateInventoryTitle(player, formattedTitle);
        });
    }

    // 构建格式化标题
    private String buildFormattedTitle() {
        MagicLib magicLib = MagicLib.getInstance();
        OffsetManager offsetManager = magicLib.getOffsetManager();
        AdvanceManager advanceManager = magicLib.getAdvanceManager();
        StringBuilder builder = new StringBuilder();
        handleTitle(title).forEach((key, value) -> {
            int offset = getConfigValue(OFFSET_PATH, key, 0, ParseType.INTEGER);
            int textOffset = advanceManager.getAdvance(value);
            boolean center = getConfigValue(CENTER_PATH, key, false, ParseType.BOOLEAN);
            boolean left = getConfigValue(LEFT_PATH, key, false, ParseType.BOOLEAN);
            if (center) {
                int halfOffset = -(textOffset / 2);
                builder.append(offsetManager.format(halfOffset, StringUtil.EMPTY))
                        .append(offsetManager.format(offset, value))
                        .append(offsetManager.format(-(offset + halfOffset + textOffset), StringUtil.EMPTY));
            } else if (left) {
                int leftOffset = - textOffset;
                builder.append(offsetManager.format(leftOffset, StringUtil.EMPTY))
                        .append(offsetManager.format(offset, value))
                        .append(offsetManager.format(-(offset + leftOffset + textOffset), StringUtil.EMPTY));
            } else {
                builder.append(offsetManager.format(offset, value))
                        .append(offsetManager.format(-(offset + textOffset), StringUtil.EMPTY));
            }
        });
        return builder.toString();
    }

    // 辅助方法：获取配置值
    @SuppressWarnings("SameParameterValue")
    private <T> T getConfigValue(String pathFormat, String key, T defaultValue, ParseType parseType) {
        return getConfigValue(String.format(pathFormat, key), defaultValue, parseType);
    }

    // 返回菜单
    protected void backMenu() {
        if (backMenuRunnable == null) {
            asyncCloseMenu();
            return;
        }
        this.disabledCloseSound = true;
        backMenuRunnable.run();
    }

    // 播放声音
    protected void asyncPlaySound(String path) {
        CompletableFuture.runAsync(() -> {
            if (!plugin.getConfigManager().containsYmlKey(configPath, path + SOUND_PATH_SUFFIX)) {
                return;
            }

            NMSDispatcher.getInstance().playSound(player,
                    plugin.getConfigManager().getYmlValue(configPath, path + SOUND_PATH_SUFFIX),
                    getConfigValue(path + VOLUME_PATH_SUFFIX, 1.0F, ParseType.FLOAT),
                    getConfigValue(path + PITCH_PATH_SUFFIX, 1.0F, ParseType.FLOAT),
                    getConfigValue(path + SPEED_PATH_SUFFIX, 1L, ParseType.LONG)
            );
        });
    }

    // 辅助方法：获取配置值（泛型版本）
    public  <T> T getConfigValue(String path, T defaultValue, ParseType parseType) {
        return plugin.getConfigManager().getYmlValue(configPath, path, defaultValue, parseType);
    }

    // InventoryHolder 实现
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    // 事件处理
    @Override
    public void openEvent(InventoryOpenEvent e) {
        asyncPlaySound("Sound.Open");
    }

    @Override
    public void closeEvent(InventoryCloseEvent e) {
        cooldownManager.destroy();
        if (!disabledCloseSound) {
            asyncPlaySound("Sound.Close");
        }
    }

    // 物品槽位操作
    public void setItemSlot(int slot, ItemStack itemStack) {
        if (!cooldownManager.getCooldowns().containsKey(slot)) {
            inventory.setItem(slot, itemStack);
        }
        cooldownManager.getCooldowns().computeIfPresent(slot, (k, v) -> itemStack);
    }

    public void setItemSlot(int slot, ItemFlag... itemFlags) {
        char slotChar = layout.charAt(slot);
        setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManager(),
                configPath, "Icons." + slotChar + ".Display"));
    }

    public void setButtonSlot(int slot, boolean disabled, ItemFlag... itemFlags) {
        char slotChar = layout.charAt(slot);
        String path = "Icons." + slotChar + (disabled ? ".DisabledDisplay" : ".ActiveDisplay");
        setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManager(), configPath, path));
    }

    public void setCheckBoxSlot(int slot, boolean opened, ItemFlag... itemFlags) {
        char slotChar = layout.charAt(slot);
        String path = "Icons." + slotChar + (opened ? ".OpenDisplay" : ".CloseDisplay");
        setItemSlot(slot, ItemUtil.getItemStackByConfig(plugin.getConfigManager(), configPath, path));
    }
}
