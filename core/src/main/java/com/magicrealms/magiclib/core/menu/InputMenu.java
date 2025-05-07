package com.magicrealms.magiclib.core.menu;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.dispatcher.NMSDispatcher;
import com.magicrealms.magiclib.core.entity.InputValidatorResult;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.scheduler.BukkitTask;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Ryan-0916
 * @Desc 输入菜单
 * @date 2024-07-17
 */
@SuppressWarnings("unused")
public class InputMenu {

    private final MagicRealmsPlugin PLUGIN;
    private final Player PLAYER;
    private final ItemStack ITEM_STACK;
    private final Consumer<String> CONFIRM_CONSUMER;
    private final Runnable CANCEL_RUNNABLE;
    private final Function<String, InputValidatorResult> CUSTOM_VALIDATOR;
    private final InventoryView INVENTORY_VIEW;
    private final int RESULT_SLOT;
    private final int OBSERVATION_TIME;
    /* 使用等待并观察的策略检测是否为最重输入文本，
    当用户连续输入文本的间隔太快时，我将尝试次策略节省重复的判断 */
    private BukkitTask observationTask;
    private String renameText;
    private String realPlaceholder;

    private InputMenu(Builder builder) {
        this.PLUGIN = builder.plugin;
        this.PLAYER = builder.player;
        this.ITEM_STACK = builder.itemStack;
        this.CONFIRM_CONSUMER = builder.confirmConsumer;
        this.CANCEL_RUNNABLE = builder.cancelRunnable;
        this.CUSTOM_VALIDATOR = builder.customValidator;
        this.realPlaceholder = StringUtil.EMPTY;
        this.RESULT_SLOT = Math.min(Math.max(builder.resultSlot, 1), 2);
        this.INVENTORY_VIEW = NMSDispatcher.getInstance().openAnvil(PLAYER, Map.of(0, ITEM_STACK), realPlaceholder);
        this.OBSERVATION_TIME = 5;
        Bukkit.getPluginManager().registerEvents(new Listener() {

            /* 铁砧改名事件 */
            @SuppressWarnings("UnstableApiUsage")
            @EventHandler
            public void onPrepareInventoryResultEvent(PrepareAnvilEvent e) {
                if (e.getView() != INVENTORY_VIEW) return;
                /* 双方不一致并且有一方不为空的情况下就应该进入*/
                if (!StringUtils.equals(renameText, e.getView().getRenameText()) &&
                        (StringUtils.isNotBlank(renameText) || StringUtils.isNotBlank(e.getView().getRenameText()))) {
                    renameText = e.getView().getRenameText();
                    e.setResult(ItemUtil.AIR);
                    if (observationTask != null && !observationTask.isCancelled()) {
                        observationTask.cancel();
                    }
                    observationTask = Bukkit.getScheduler().runTaskLater(PLUGIN, () -> {
                        InputValidatorResult result = CUSTOM_VALIDATOR.apply(renameText);
                        /* 当答案不正确时并且文本没变化时阻止 */
                        if (!result.valid() && realPlaceholder.equals(result.message())) {
                            return;
                        }
                        ItemMeta itemMeta = ITEM_STACK.getItemMeta();
                        itemMeta.displayName(Component.text(renameText));
                        ITEM_STACK.setItemMeta(itemMeta);
                        /* 如果验证成功且文本已变化，更新物品到相应槽位 */
                        if (result.valid() && realPlaceholder.equals(result.message())) {
                            e.getInventory().setFirstItem(ITEM_STACK);
                            if (RESULT_SLOT == 1)  e.getInventory().setSecondItem(ITEM_STACK);
                            else  e.getInventory().setResult(ITEM_STACK);
                        } else {
                            setupAnvil(ITEM_STACK, RESULT_SLOT == 1 && result.valid() ? ITEM_STACK : ItemUtil.AIR,
                                    RESULT_SLOT == 2 && result.valid() ? ITEM_STACK : ItemUtil.AIR, result.message());
                        }
                        realPlaceholder = result.message();
                    }, OBSERVATION_TIME);
                }
            }
            /* 铁砧点击事件 */
            @SuppressWarnings("UnstableApiUsage")
            @EventHandler
            public void onInventoryClickEvent(InventoryClickEvent e) {
                if (e.getView() != INVENTORY_VIEW) return;
                if (e.getView() instanceof AnvilView anvilInventory) {
                    e.setCancelled(true);
                    if (ItemUtil.isAirOrNull(e.getCurrentItem()) || e.getSlot() != RESULT_SLOT) return;
                    Bukkit.getScheduler().runTask(PLUGIN, () -> {
                        PLAYER.closeInventory();
                        CONFIRM_CONSUMER.accept(anvilInventory.getRenameText());
                    });
                }
            }
            /* 铁砧关闭事件 */
            @EventHandler
            public void onInventoryCloseEvent(InventoryCloseEvent e) {
                if (e.getView() != INVENTORY_VIEW) return;
                if (e.getInventory() instanceof AnvilInventory anvilInventory) {
                    HandlerList.unregisterAll(this);
                    anvilInventory.setFirstItem(ItemUtil.AIR);
                    anvilInventory.setResult(ItemUtil.AIR);
                    anvilInventory.setSecondItem(ItemUtil.AIR);
                    if (observationTask != null && !observationTask.isCancelled()) observationTask.cancel();
                    Bukkit.getScheduler().runTask(PLUGIN, CANCEL_RUNNABLE);
                }
            }
        }, PLUGIN);
    }

    private void setupAnvil(@Nullable ItemStack firstItem, @Nullable ItemStack secondItem, @Nullable ItemStack resultItem, String placeholder) {
        Map<Integer, ItemStack> map = new HashMap<>();
        if(firstItem != null) map.put(0, firstItem);
        if(secondItem != null) map.put(1, secondItem);
        if(resultItem != null) map.put(2, resultItem);
        Bukkit.getScheduler().runTask(PLUGIN, () -> NMSDispatcher.getInstance().setupAnvil(PLAYER,
                map, realPlaceholder));
    }

    public static class Builder {
        private MagicRealmsPlugin plugin;
        private Player player;
        private ItemStack itemStack;
        private Consumer<String> confirmConsumer;
        private Runnable cancelRunnable;
        private int resultSlot;

        private Function<String, InputValidatorResult> customValidator;

        public Builder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public Builder confirmConsumer(Consumer<String> confirmConsumer) {
            this.confirmConsumer = confirmConsumer;
            return this;
        }

        public Builder cancelTask(Runnable cancelTask) {
            this.cancelRunnable = cancelTask;
            return this;
        }

        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        public Builder customValidator(Function<String, InputValidatorResult> customValidator) {
            this.customValidator = customValidator;
            return this;
        }

        public Builder plugin(MagicRealmsPlugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder resultSlot(int resultSlot) {
            this.resultSlot = resultSlot;
            return this;
        }

        public void open() {
            if (plugin == null || player == null
                    || itemStack == null
                    || confirmConsumer == null
                    || cancelRunnable == null
                    || customValidator == null)
                throw new NullPointerException("构建输入菜单时出现未知异常，请填写必填参数");
            new InputMenu(this);
        }
    }
}
