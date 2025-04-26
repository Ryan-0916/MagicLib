package com.magicrealms.magiclib.mc_1_21_R3.message;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.message.AbstractMessage;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Ryan-0916
 * @Desc 图腾消息
 * @date 2024-05-25
 */
public class TotemMessage extends AbstractMessage {

    private static volatile TotemMessage INSTANCE;

    private final ResourceLocation SOUND_KEY;

    private TotemMessage() {
        this.SOUND_KEY = ResourceLocation.withDefaultNamespace("item.totem.use");
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
     * 给玩家发送一条 Totem 消息
     * @param plugin 要发送消息的插件 {@link MagicRealmsPlugin}
     * @param player 要接收消息的玩家对象
     * @param message 消息内容，内容信息如下
     * 使用方法:
     * <totem></totem> 发送一条图腾消息给玩家
     * 内连属性:
     * <modelData>1</modelData> 图腾材质数据，默认值：0
     */
    @Override
    public void sendMessage(MagicRealmsPlugin plugin, Player player, String message) {
        int modelData = StringUtil.getValueBetweenTags(message, "modelData", 0, ParseType.INTEGER);
        ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta itemMeta = totem.getItemMeta();
        itemMeta.setCustomModelData(modelData);
        totem.setItemMeta(itemMeta);
        sendTotem(player, totem);
    }

    public void sendTotem(Player player, ItemStack totem) {
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
    public void cleanMessage(Player player) {
    }
}
