package com.magicrealms.magiclib.mc_1_20_R1.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Ryan-0916
 * @Desc 图腾消息
 * @date 2024-05-25
 **/
public class TotemMessage extends AbstractMessage {

    private static volatile TotemMessage INSTANCE;
    private final ResourceLocation SOUND_KEY;

    private TotemMessage() {
        this.SOUND_KEY = new ResourceLocation(Sound.ITEM_TOTEM_USE.key().value());
    }

    public static TotemMessage getInstance() {
        if (INSTANCE == null) {
            synchronized (TotemMessage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TotemMessage();
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
     * <totem><modelData>0</modelData></totem> 发送一条图腾消息给玩家
     */
    @Override
    public void sendMessage(@NotNull MagicRealmsPlugin plugin, @NotNull Player player, @NotNull String message) {
        int modelData = StringUtil.getIntegerBTWTags(message, "modelData", 0);
        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta itemMeta = totem.getItemMeta();
        itemMeta.setCustomModelData(modelData);
        totem.setItemMeta(itemMeta);
        sendTotem(player, totem);
    }

    public void sendTotem(@NotNull Player player, @NotNull ItemStack totem) {
        ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();
        serverPlayer.connection.send(new ClientboundSetEquipmentPacket(player.getEntityId(), List.of(Pair.of(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(totem)))));
        Stream.of(new Thread(() -> {
            ItemStack previousItem = player.getInventory().getItemInOffHand();
            serverPlayer.connection.send(new ClientboundBundlePacket(List.of(
                    new ClientboundEntityEventPacket(serverPlayer, (byte)35),
                    new ClientboundSetEquipmentPacket(player.getEntityId(), List.of(Pair.of(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(previousItem))))
            )));
        }), new Thread(() -> serverPlayer.connection.send(new ClientboundBundlePacket(List.of(
                new ClientboundStopSoundPacket(SOUND_KEY, SoundSource.PLAYERS)
        ))))).forEach(Thread::start);
    }


    @Override
    public void cleanMessage(@NotNull Player player) {

    }
}
