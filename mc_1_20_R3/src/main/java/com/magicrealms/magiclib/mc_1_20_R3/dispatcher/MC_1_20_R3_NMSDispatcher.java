package com.magicrealms.magiclib.mc_1_20_R3.dispatcher;

import com.magicrealms.magiclib.bukkit.dispatcher.INMSDispatcher;
import com.magicrealms.magiclib.mc_1_20_R3.utils.ComponentUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 具体NMS调度器 - 对应 MC 版本 1.20.3、1.20.4
 * @date 2024-07-17
 */
@SuppressWarnings("unused")
public class MC_1_20_R3_NMSDispatcher implements INMSDispatcher {

    private static volatile MC_1_20_R3_NMSDispatcher INSTANCE;

    private MC_1_20_R3_NMSDispatcher() {}

    public static MC_1_20_R3_NMSDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized(MC_1_20_R3_NMSDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MC_1_20_R3_NMSDispatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取容器类型
     * @param inventory 容器
     */
    private MenuType<?> getCraftInventoryType(Inventory inventory) {
        return switch (inventory.getType()) {
            case CHEST -> switch (inventory.getSize() / 9) {
                    case 1 -> MenuType.GENERIC_9x1;
                    case 2 -> MenuType.GENERIC_9x2;
                    case 3 -> MenuType.GENERIC_9x3;
                    case 4 -> MenuType.GENERIC_9x4;
                    case 5 -> MenuType.GENERIC_9x5;
                    default -> MenuType.GENERIC_9x6;
                };
            case DISPENSER, DROPPER -> MenuType.GENERIC_3x3;
            case FURNACE -> MenuType.FURNACE;
            case WORKBENCH -> MenuType.CRAFTING;
            case ENCHANTING -> MenuType.ENCHANTMENT;
            case BREWING -> MenuType.BREWING_STAND;
            case MERCHANT -> MenuType.MERCHANT;
            case ANVIL -> MenuType.ANVIL;
            case SMITHING -> MenuType.SMITHING;
            case BEACON -> MenuType.BEACON;
            case HOPPER -> MenuType.HOPPER;
            case SHULKER_BOX -> MenuType.SHULKER_BOX;
            case BLAST_FURNACE -> MenuType.BLAST_FURNACE;
            case LECTERN -> MenuType.LECTERN;
            case SMOKER -> MenuType.SMOKER;
            case LOOM -> MenuType.LOOM;
            case CARTOGRAPHY -> MenuType.CARTOGRAPHY_TABLE;
            case GRINDSTONE -> MenuType.GRINDSTONE;
            case STONECUTTER -> MenuType.STONECUTTER;
            default -> MenuType.GENERIC_9x3;
        };
    }

    @Override
    public void openCustomInventory(Player player, Inventory inventory, String title) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        MenuType<?> menuType = getCraftInventoryType(inventory);
        AbstractContainerMenu menu = new CraftContainer(inventory, serverPlayer, serverPlayer.nextContainerCounter());
        menu = CraftEventFactory.callInventoryOpenEvent(serverPlayer, menu);
        if (menu != null) {
            menu.checkReachable = false;
            serverPlayer.connection.send(new ClientboundOpenScreenPacket(menu.containerId, menuType,
                    ComponentUtil.getComponentOrEmpty(title)));
            serverPlayer.containerMenu = menu;
            serverPlayer.initMenu(menu);
        }
    }

    @Override
    public void updateInventoryTitle(Player player, String title) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        AbstractContainerMenu menu = serverPlayer.containerMenu;
        serverPlayer.connection.send(new ClientboundOpenScreenPacket(menu.containerId,
                menu.getType(), ComponentUtil.getComponentOrEmpty(title)));
        serverPlayer.initMenu(menu);
    }

    @Override
    public InventoryView openAnvil(Player player, Map<Integer, ItemStack> anvilItems, String title) {
        InventoryView inventoryView = player.openAnvil(player.getLocation(), true);
        setupAnvil(player, anvilItems, title);
        return inventoryView;
    }

    @Override
    public void setupAnvil(Player player, Map<Integer, ItemStack> anvilItems, String title) {
        InventoryView inventoryView = player.getOpenInventory();
        if (inventoryView.getType() != InventoryType.ANVIL) return;
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        serverPlayer.connection.send(new ClientboundOpenScreenPacket(
                serverPlayer.containerMenu.containerId,
                MenuType.ANVIL,
                ComponentUtil.getComponentOrEmpty(title)));
        if (!anvilItems.isEmpty() && inventoryView.getTopInventory() instanceof AnvilInventory anvilInventory) {
            anvilItems.forEach((i, e) -> {
                switch (i) {
                    case 0 -> anvilInventory.setFirstItem(e);
                    case 1 -> anvilInventory.setSecondItem(e);
                    case 2 -> anvilInventory.setResult(e);
                }
            });
        }
        player.updateInventory();
    }

    @Override
    public void resetChatDialog(Player player, List<String> messageHistory) {
        int historySize = Math.min(messageHistory.size(), 100);
        List<Packet<ClientGamePacketListener>> packets = new ArrayList<>(historySize + 1);
        if (historySize < 100) {
            String emptyMessage = "\n".repeat(100 - historySize);
            packets.add(new ClientboundSystemChatPacket(CraftChatMessage.fromJSONOrString(emptyMessage, true), false));
        }
        messageHistory.stream()
                .skip(Math.max(0, messageHistory.size() - historySize))
                .limit(historySize)
                .map(msg -> new ClientboundSystemChatPacket(ComponentUtil.getComponentOrEmpty(msg), false))
                .forEach(packets::add);
        ((CraftPlayer) player).getHandle().connection.send(new ClientboundBundlePacket(packets));
    }

    @Override
    public void playSound(Player player, String namespace, float volume, float pitch, long speed) {
        ((CraftPlayer)player).getHandle().connection.send(new ClientboundSoundPacket(Holder.direct(SoundEvent.createVariableRangeEvent(new ResourceLocation(namespace))),
                SoundSource.PLAYERS, player.getLocation().getX(),
                player.getLocation().getY(), player.getLocation().getZ(),
                volume, pitch, speed));
    }

    @Override
    public void setItemCooldown(Player player, ItemStack item, int duration) {
        ((CraftPlayer)player).getHandle().connection.send(new ClientboundCooldownPacket(
                CraftItemStack.asNMSCopy(item).getItem(), duration
        ));
    }

    @Override
    public void removeItemCooldown(Player player, ItemStack item) {
        ((CraftPlayer)player).getHandle().connection.send(new ClientboundCooldownPacket(
                CraftItemStack.asNMSCopy(item).getItem(), 0
        ));
    }
}
