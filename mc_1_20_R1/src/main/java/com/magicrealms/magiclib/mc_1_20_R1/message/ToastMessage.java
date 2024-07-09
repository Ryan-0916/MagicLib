package com.magicrealms.magiclib.mc_1_20_R1.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Ryan-0916
 * @Desc 成就消息
 * @date 2024-05-06
 **/
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
     * @param plugin 发送消息的插件
     * @param player 消息接收者
     * @param message 消息内容，内容信息如下
     * 使用方法:
     * <toast>Hello world</toast> 发送一条 Hello world 的成就信息给玩家
     * 内连属性:
     * <material>NAME_TAG</material> 成就信息图标，默认：命名牌
     * <modelData>1</modelData> 图标材质数据，默认：0
     * <type>TASK</type> 成就类型 TASK CHALLENGE GOAL，默认：TASK
     * <legacy>true</legacy> 是否需要支持 &x&F&F&F&F&F&F 的写法
     * <command>tpa admin</command> 按 F 执行的指令，默认：不执行
     * <inValidateTime>7</inValidateTime> F 的判断时间 （秒）
     */
    @Override
    public void sendMessage(@NotNull MagicRealmsPlugin plugin, @NotNull Player player, @NotNull String message) {
        cleanMessage(player);
        /* 获取消息发送的循环次数、循环间隔、图标、类型等 */
        int modelData = StringUtil.getIntegerBTWTags(message, "modelData", 0);
        double inValidateTime = StringUtil.getDoubleBTWTags(message, "inValidateTime", 7D);
        boolean legacy = StringUtil.getBooleanBTWTags(message, "legacy", true);
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
        FrameType type = Optional.of(
                FrameType.valueOf(StringUtil.getStringBTWTags(message, "type")
                        .orElse("TASK"))).orElse(FrameType.TASK);

        /* 发送成就信息 */
        sendToast(player, icon, StringUtil.removeTags(AdventureHelper.serializeComponent(AdventureHelper.deserializeComponent(
                legacy ? AdventureHelper.legacyToMiniMessage(message) : message)), "material", "modelData", "type", "command", "legacy", "inValidateTime"), type);

        commandOptional.ifPresent(command -> {
            Listener listener = new Listener() {
                @EventHandler
                public void onPlayerSwapHandItemsEvent (PlayerSwapHandItemsEvent e) {
                    if (player.getUniqueId().equals(e.getPlayer().getUniqueId())) {
                        Bukkit.dispatchCommand(player, command);
                        cleanMessage(player);
                    }
                    e.setCancelled(true);
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
    public void cleanMessage(@NotNull Player player) {
        Optional.ofNullable(LISTENER.get(player.getUniqueId())).ifPresent(listener -> {
            LISTENER.remove(player.getUniqueId());
            HandlerList.unregisterAll(listener);
        });
        Optional.ofNullable(TASK.get(player.getUniqueId())).ifPresent(task -> {
            TASK.remove(player.getUniqueId());
            if (!task.isCancelled()) task.cancel();
        });
    }

    private void sendToast(@NotNull Player player, @NotNull ItemStack icon, @NotNull String msg, @NotNull FrameType type) {
        DisplayInfo displayInfo = new DisplayInfo(
                CraftItemStack.asNMSCopy(icon),
                Optional.ofNullable(Component.Serializer.fromJson(msg)).orElse(Component.empty()),
                Component.empty(),
                null,
                type, true, false, true);
        Advancement advancement = new Advancement(MINECRAFT_KEY,
                null,
                displayInfo,
                AdvancementRewards.EMPTY,
                new HashMap<>(Map.of("impossible", new Criterion(new ImpossibleTrigger.TriggerInstance()))),
                new String[][]{{"impossible"}},
                false);
        Map<ResourceLocation, AdvancementProgress> advancementsToGrant = new HashMap<>();
        AdvancementProgress advancementProgress = new AdvancementProgress();
        advancementProgress.update(advancement.getCriteria(), advancement.getRequirements());
        Objects.requireNonNull(advancementProgress.getCriterion("impossible")).grant();
        advancementsToGrant.put(MINECRAFT_KEY, advancementProgress);
        List<Packet<ClientGamePacketListener>> packets = List.of(
                new ClientboundUpdateAdvancementsPacket(
                false,
                        new ArrayList<>(List.of(advancement)),
                        new HashSet<>(),
                        advancementsToGrant)
        , new ClientboundUpdateAdvancementsPacket(
                false,
                        new ArrayList<>(),
                        new HashSet<>(List.of(MINECRAFT_KEY)),
                        new HashMap<>()));
        ((CraftPlayer)player).getHandle().connection.send(new ClientboundBundlePacket(packets));
    }
}
