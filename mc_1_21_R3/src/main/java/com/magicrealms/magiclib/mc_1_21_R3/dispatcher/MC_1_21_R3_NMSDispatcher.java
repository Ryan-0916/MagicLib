package com.magicrealms.magiclib.mc_1_21_R3.dispatcher;

import com.magicrealms.magiclib.common.dispatcher.INMSDispatcher;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-01
 */
@SuppressWarnings("unused")
public class MC_1_21_R3_NMSDispatcher implements INMSDispatcher {

    private static volatile  MC_1_21_R3_NMSDispatcher INSTANCE;

    public static MC_1_21_R3_NMSDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (MC_1_21_R3_NMSDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MC_1_21_R3_NMSDispatcher();
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
            serverPlayer.connection.send(new ClientboundOpenScreenPacket(menu.containerId, menuType, CraftChatMessage.fromJSON(title)));
            serverPlayer.containerMenu = menu;
            serverPlayer.initMenu(menu);
        }
    }

    @Override
    public void updateInventoryTitle(Player player, String title) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        AbstractContainerMenu menu = serverPlayer.containerMenu;
        serverPlayer.connection.send(new ClientboundOpenScreenPacket(menu.containerId,
                menu.getType(), CraftChatMessage.fromJSON(title)));
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
                CraftChatMessage.fromJSON(title)));
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
}
