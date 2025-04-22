package com.magicrealms.magiclib.mc_1_20_R3.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.mc_1_20_R3.utils.ComponentUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;


import java.util.*;

/**
 * @author Ryan-0916
 * @date 2024-05-06
 */
public class ToastMessage extends AbstractMessage {

    private static volatile ToastMessage INSTANCE;
    private final ResourceLocation MINECRAFT_KEY;
    private final Map<UUID, Listener> LISTENER;
    private final Map<UUID, BukkitTask> TASK;

    private ToastMessage() {
        this.MINECRAFT_KEY = new ResourceLocation("magiclib", "toast");
        this.LISTENER = new HashMap<>();
        this.TASK = new HashMap<>();
    }

    public static ToastMessage getInstance() {
        if (INSTANCE == null) {
            synchronized (ToastMessage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ToastMessage();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 给玩家发送一条 Toast 消息
     * @param plugin 要发送消息的插件 {@link MagicRealmsPlugin}
     * @param player 要接收消息的玩家对象
     * @param message 消息内容，内容信息如下
     * 使用方法:
     * <toast>Hello world</toast> 发送一条 Hello world 的 Toast 消息给玩家
     * 内连属性:
     * <legacy>false</legacy> 是否使用旧版的MiniMessage格式进行序列化例如颜色 &x&F&F&F&F&F&F 的写法，默认值：false
     * <material>NAME_TAG</material> 成就信息图标，默认值：NAME_TAG
     * <modelData>1</modelData> 图标材质数据，默认值：0
     * <type>TASK</type> 成就类型 TASK CHALLENGE GOAL，默认值：TASK
     * <command>tpa admin</command> 按 F 执行的指令，默认值：不执行
     * <inValidateTime>7</inValidateTime> F 的判断时间 （秒），默认值：7D
     */
    @Override
    public void sendMessage(MagicRealmsPlugin plugin, Player player, String message) {
        cleanMessage(player);
        /* 获取消息发送的循环次数、循环间隔、图标、类型等 */
        int modelData = StringUtil.getValueBTWTags(message, "modelData", 0, ParseType.INTEGER);
        double inValidateTime = StringUtil.getValueBTWTags(message, "inValidateTime", 7D, ParseType.DOUBLE);
        boolean legacy = StringUtil.getValueBTWTags(message, "legacy", false, ParseType.DOUBLE);
        Optional<String> commandOptional = StringUtil.getStringBTWTags(message, "command");
        ItemStack icon = new ItemStack(Optional.of(Material.valueOf(StringUtil.getStringBTWTags(message, "material")
                .orElse("NAME_TAG"))).orElse(Material.NAME_TAG));

        /* 设置图标的材质信息 */
        if (modelData != 0) {
            ItemMeta meta = icon.getItemMeta();
            meta.setCustomModelData(modelData);
            icon.setItemMeta(meta);
        }

        /* 拿到成就信息的类型 */
        AdvancementType type = Optional.of(
                AdvancementType.valueOf(StringUtil.getStringBTWTags(message, "type")
                        .orElse("TASK"))).orElse(AdvancementType.TASK);

        /* 发送成就信息 */
        sendToast(player, icon, StringUtil.removeTags(AdventureHelper.serializeComponent(AdventureHelper.deserializeComponent(
                legacy ? AdventureHelper.legacyToMiniMessage(message) : message)), "material", "modelData", "type", "command", "legacy", "inValidateTime"), type);

        commandOptional.ifPresent(command -> {
            Listener listener = new Listener() {
                @EventHandler(priority = EventPriority.LOWEST)
                public void onPlayerSwapHandItemsEvent (PlayerSwapHandItemsEvent e) {
                    if (e.isCancelled()) return;
                    if (player.getUniqueId().equals(e.getPlayer().getUniqueId())) {
                        Bukkit.dispatchCommand(player, command);
                        cleanMessage(player);
                        e.setCancelled(true);
                    }
                }
            };
            LISTENER.put(player.getUniqueId(), listener);
            TASK.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(plugin, () -> {
                LISTENER.remove(player.getUniqueId());
                HandlerList.unregisterAll(listener);
            }, Math.round(inValidateTime * 20)));
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        });
    }

    /**
     * 移除玩家全部等待发送的 Toast 消息
     * NMS 不支持移除已显示的内容 因此此行作废
     * @param player 玩家
     */
    @Override
    public void cleanMessage(Player player) {
        Optional.ofNullable(LISTENER.get(player.getUniqueId())).ifPresent(listener -> {
            LISTENER.remove(player.getUniqueId());
            HandlerList.unregisterAll(listener);
        });
        Optional.ofNullable(TASK.get(player.getUniqueId())).ifPresent(task -> {
            TASK.remove(player.getUniqueId());
            if (!task.isCancelled()) task.cancel();
        });
    }

    private void sendToast(Player player, ItemStack icon, String msg, AdvancementType type) {
        Optional<DisplayInfo> displayInfo = Optional.of(
                new DisplayInfo(
                        CraftItemStack.asNMSCopy(icon),
                        ComponentUtil.getComponentOrEmpty(msg),
                        Component.empty(),
                        Optional.empty(),
                        type, true, false, true));
        Advancement advancement = new Advancement(Optional.empty(),
                displayInfo,
                AdvancementRewards.EMPTY,
                Map.of("impossible", new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance())),
                new AdvancementRequirements(List.of(List.of("impossible")))
                , false);
        AdvancementProgress advancementProgress = new AdvancementProgress();
        advancementProgress.update(advancement.requirements());
        Objects.requireNonNull(advancementProgress.getCriterion("impossible")).grant();
        List<Packet<ClientGamePacketListener>> packets = List.of(new ClientboundUpdateAdvancementsPacket(
                false,
                List.of(new AdvancementHolder(MINECRAFT_KEY, advancement)),
                new HashSet<>(),
                Map.of(MINECRAFT_KEY, advancementProgress)
        ), new ClientboundUpdateAdvancementsPacket(
                false,
                new ArrayList<>(),
                new HashSet<>(List.of(MINECRAFT_KEY)),
                new HashMap<>()
        ));
        ((CraftPlayer)player).getHandle().connection.send(new ClientboundBundlePacket(packets));
    }
}
