package com.magicrealms.magiclib.paper.dispatcher;

import com.magicrealms.magiclib.common.dispatcher.INMSDispatcher;
import com.magicrealms.magiclib.common.exception.UnsupportedVersionException;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.mc_1_20_R1.dispatcher.MC_1_20_R1_NMSDispatcher;
import com.magicrealms.magiclib.mc_1_20_R3.dispatcher.MC_1_20_R3_NMSDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc NMS 调度器
 * @date 2024-07-17
 */
public class NMSDispatcher implements INMSDispatcher {

    private static volatile NMSDispatcher INSTANCE;

    private final INMSDispatcher NMS_DISPATCHER;

    private NMSDispatcher() {
        NMS_DISPATCHER = switch (Bukkit.getServer().getBukkitVersion().split("-")[0]) {
            case "1.20.1" -> MC_1_20_R1_NMSDispatcher.getInstance();
            case "1.20.3", "1.20.4" -> MC_1_20_R3_NMSDispatcher.getInstance();
            default -> throw new UnsupportedVersionException("您的 Minecraft 版本不兼容，请使用合适的版本");
        };
    }

    public static NMSDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized(NMSDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NMSDispatcher();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void updateInventoryTitle(@NotNull Player player, @NotNull String title) {
        NMS_DISPATCHER.updateInventoryTitle(player, AdventureHelper.serializeComponent(
                AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(title))));
    }

    @Override
    public InventoryView openAnvil(@NotNull Player player,
                                   @NotNull Map<Integer, ItemStack> anvilItems, @NotNull String title) {
        return NMS_DISPATCHER.openAnvil(player, anvilItems, AdventureHelper.serializeComponent(
                AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(title))));
    }

    @Override
    public void setupAnvil(@NotNull Player player,
                           @NotNull Map<Integer, ItemStack> anvilItems, @NotNull String title) {
        NMS_DISPATCHER.setupAnvil(player, anvilItems, AdventureHelper.serializeComponent(
                AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(title))));
    }


}
