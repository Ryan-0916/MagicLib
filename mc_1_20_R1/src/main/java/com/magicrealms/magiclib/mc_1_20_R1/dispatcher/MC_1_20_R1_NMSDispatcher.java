package com.magicrealms.magiclib.mc_1_20_R1.dispatcher;

import com.magicrealms.magiclib.common.dispatcher.INMSDispatcher;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 具体NMS调度器 - 对应 MC 版本 1.20.1
 * @date 2024-07-17
 */
@SuppressWarnings("unused")
public class MC_1_20_R1_NMSDispatcher implements INMSDispatcher {

    private static volatile MC_1_20_R1_NMSDispatcher INSTANCE;

    private MC_1_20_R1_NMSDispatcher() {}

    public static MC_1_20_R1_NMSDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized(MC_1_20_R1_NMSDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MC_1_20_R1_NMSDispatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 获取容器类型
     * @param inventory 容器
     */
    private MenuType<?> getCraftInventoryType(@NotNull Inventory inventory) {
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
    public void openCustomInventory(@NotNull Player player, @NotNull Inventory inventory, @NotNull String title) {
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
    public void updateInventoryTitle(@NotNull Player player, @NotNull String title) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        AbstractContainerMenu menu = serverPlayer.containerMenu;
        serverPlayer.connection.send(new ClientboundOpenScreenPacket(menu.containerId,
                menu.getType(), CraftChatMessage.fromJSON(title)));
        serverPlayer.initMenu(menu);
    }

    @Override
    public InventoryView openAnvil(@NotNull Player player, @NotNull Map<Integer, ItemStack> anvilItems, @NotNull String title) {
        openCustomInventory(player, Bukkit.createInventory(null, InventoryType.ANVIL), title);
        setupAnvil(player, anvilItems, title);
        return player.getOpenInventory();
    }

    @Override
    public void setupAnvil(@NotNull Player player, @NotNull Map<Integer, ItemStack> anvilItems, @NotNull String title) {
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
                    case 0: anvilInventory.setFirstItem(e);
                    case 1: anvilInventory.setSecondItem(e);
                    case 2: anvilInventory.setResult(e);
                }
            });
        }
        player.updateInventory();
    }

}
