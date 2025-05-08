package com.magicrealms.magiclib.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.magicrealms.magiclib.common.utils.Base64Util;
import com.magicrealms.magiclib.core.adapt.ItemStackGsonAdapter;
import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.saicone.rtag.item.ItemTagStream;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @Desc 物品工具类
 * @date 2024-05-27
 */
@SuppressWarnings("unused")
@Slf4j
public final class ItemUtil {

    private ItemUtil() {}

    public static final ItemStack AIR = new ItemStack(Material.AIR);

    public static final ItemTagStream tag = ItemTagStream.INSTANCE;

    public static final Component UN_ITALIC
            = Component.text(StringUtil.EMPTY,
            Style.style(TextDecoration.ITALIC.withState(false)));

    public static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            /* 自定义处理器 */
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackGsonAdapter())
            .create();

    /* 序列化物品并克隆 */
    public static Optional<String> serializer(ItemStack itemStack) {
        return serializerUnClone(itemStack.clone());
    }

    /* 序列化物品 */
    public static Optional<String> serializerUnClone(ItemStack itemStack) {
        return Optional.ofNullable(Base64Util.stringToBase64(tag.toString(itemStack)));
    }

    /* 反序列化物品 */
    public static Optional<ItemStack> deserializer(String deserializer) {
        return Optional.ofNullable(tag.fromString(Base64Util.base64ToString(deserializer)));
    }

    /* 获取玩家头颅 */
    public static ItemStack getPlayerHead(Player player) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        if (itemStack.getItemMeta() instanceof SkullMeta skull) {
            skull.setOwningPlayer(player);
            itemStack.setItemMeta(skull);
        }
        return itemStack;
    }

    /* 是空气或为空 */
    public static boolean isAirOrNull(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
    }

    /* 不是空气并且不为空 */
    public static boolean isNotAirOrNull(@Nullable ItemStack itemStack) {
        return !isAirOrNull(itemStack);
    }

    public static ItemStack getItemStackByConfig(
            ConfigManager configManager,
            String configPath,
            String key,
            ItemFlag... itemFlags) {
        return getItemStackByConfig(configManager, configPath, key, null, null, itemFlags);
    }

    public static ItemStack getItemStackByConfig(
            ConfigManager configManager,
            String configPath,
            String key,
            Map<String, String> map,
            ItemFlag... itemFlags) {
        return getItemStackByConfig(configManager, configPath, key, map, null, itemFlags);
    }

    public static ItemStack getItemStackByConfig(
            ConfigManager configManager,
            String configPath,
            String key,
            OfflinePlayer player,
            ItemFlag... itemFlags) {
        return getItemStackByConfig(configManager, configPath, key, null, player, itemFlags);
    }

    public static ItemStack setItemStackByConfig(
            ItemStack itemStack,
            ConfigManager configManager,
            String configPath,
            String key,
            ItemFlag... itemFlags) {
        return setItemStackByConfig(itemStack, configManager, configPath, key, null, null, itemFlags);
    }

    public static ItemStack setItemStackByConfig(
            ItemStack itemStack,
            ConfigManager configManager,
            String configPath,
            String key,
            Map<String, String> map,
            ItemFlag... itemFlags) {
        return setItemStackByConfig(itemStack, configManager, configPath, key, map, null, itemFlags);
    }

    public static ItemStack setItemStackByConfig(
            ItemStack itemStack,
            ConfigManager configManager,
            String configPath,
            String key,
            OfflinePlayer player,
            ItemFlag... itemFlags) {
        return setItemStackByConfig(itemStack, configManager, configPath, key, null, player, itemFlags);
    }

    public static ItemStack getItemStackByConfig(
            ConfigManager configManager,
            String configPath,
            String key,
            @Nullable Map<String, String> map,
            @Nullable OfflinePlayer player,
            ItemFlag... itemFlags) {
        Optional<Material> material = getMaterial(configManager, configPath, key);
        Optional<String> name = getOptionalString(configManager, configPath, key, "Name");
        Optional<List<String>> lore = getOptionalList(configManager, configPath, key, "Lore");
        if (material.isEmpty()) {
            return AIR;
        }
        Builder itemBuilder = new Builder(material.get())
                .setItemFlag(itemFlags)
                .setCustomModelData(configManager.getYmlValue(configPath, key + ".ModelData", 0, ParseType.INTEGER));

        if (name.isPresent()) {
            itemBuilder = itemBuilder.setName(PlaceholderUtil
                    .replacePlaceholders(name.get(), map, player));
        }

        if (lore.isPresent()) {
            itemBuilder = itemBuilder.setLore(lore.get().stream().map(e ->
                PlaceholderUtil.replacePlaceholders(e, map, player)
            ).collect(Collectors.toList()));
        }
        return itemBuilder.builder();
    }

    @NotNull
    public static ItemStack setItemStackByConfig(
            ItemStack itemStack,
            ConfigManager configManager,
            String configPath, String key,
            @Nullable Map<String, String> map,
            @Nullable OfflinePlayer player,
            ItemFlag... itemFlags) {
        Optional<String> nameOptional = getOptionalString(configManager, configPath, key, "Name");
        Optional<List<String>> loreOptional = getOptionalList(configManager, configPath, key, "Lore");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(configManager.getYmlValue(configPath, key + ".ModelData", 0, ParseType.INTEGER));
        nameOptional.ifPresent(name -> itemMeta.displayName(UN_ITALIC.append(AdventureHelper.deserializeComponent(
                AdventureHelper.legacyToMiniMessage(PlaceholderUtil.replacePlaceholders(name, map, player))))));
        loreOptional.ifPresent(lore -> itemMeta.lore(lore.stream()
                .map(l -> UN_ITALIC.append(AdventureHelper.deserializeComponent(
                        AdventureHelper.legacyToMiniMessage(PlaceholderUtil.replacePlaceholders(l, map, player)))))
                .collect(Collectors.toList())));
        itemMeta.addItemFlags(itemFlags);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    // Helper method to extract material
    private static Optional<Material> getMaterial(ConfigManager configManager, String configPath, String key) {
        return Optional.ofNullable(Material.matchMaterial(configManager.getYmlValue(configPath, key + ".Mats")));
    }

    // Helper method to extract optional string
    @SuppressWarnings("SameParameterValue")
    private static Optional<String> getOptionalString(ConfigManager configManager, String configPath, String key, String subKey) {
        return configManager.containsYmlKey(configPath, key + "." + subKey)
                ? Optional.of(configManager.getYmlValue(configPath, key + "." + subKey))
                : Optional.empty();
    }

    // Helper method to extract optional list
    @SuppressWarnings("SameParameterValue")
    private static Optional<List<String>> getOptionalList(ConfigManager configManager, String configPath, String key, String subKey) {
        return configManager.containsYmlKey(configPath, key + "." + subKey)
                ? configManager.getYmlListValue(configPath, key + "." + subKey)
                : Optional.empty();
    }

    public static void similarItem(@Nullable ItemStack to, @Nullable ItemStack from) {
        if (isAirOrNull(to) || isAirOrNull(from)) {
            return;
        }
        int maxItemSize = to.getMaxStackSize();
        if (to.getAmount() == maxItemSize) {
            return;
        }
        if (!to.isSimilar(from)) {
            return;
        }
        if (to.getAmount() + from.getAmount() >= maxItemSize) {
            from.setAmount((to.getAmount() + from.getAmount()) - maxItemSize);
            to.setAmount(maxItemSize);
        } else {
            to.setAmount(to.getAmount() + from.getAmount());
            from.setAmount(0);
        }
    }

    public static Component getItemName(ItemStack itemStack) {
        return itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName() ?
                itemStack.getItemMeta().displayName() :
                Component.translatable((itemStack.getType().isBlock() ? "Block." : "Item.") + itemStack.getType().getKey().toString().replace(':', '.'));
    }

    public static boolean canFitIntoInventory(Player player, @Nullable ItemStack itemStack) {
        PlayerInventory playerInventory = player.getInventory();
        if (!player.isOnline()){
            return false;
        }
        if (isAirOrNull(itemStack)){
            return true;
        }
        int maxStackSize = itemStack.getMaxStackSize();
        int amount = itemStack.getAmount();
        /* 判断物品数量是否为 0 */
        if (amount == 0) {
            return true;
        }
        /* 判断背包是否存在空物品 */
        if (playerInventory.firstEmpty() != -1){
            return true;
        } else if (maxStackSize == amount) {
            return false;
        }
        AtomicInteger needFitNum = new AtomicInteger(itemStack.getAmount());
        playerInventory.all(itemStack.getType()).
                forEach((index, inventoryItem) -> {
                        if (needFitNum.get() == 0 || !inventoryItem.isSimilar(itemStack)) {
                            return;
                        }
                        if (needFitNum.get() <= maxStackSize - inventoryItem.getAmount()) {
                            needFitNum.set(0);
                        } else {
                            needFitNum.set(needFitNum.get() - (maxStackSize - inventoryItem.getAmount()));
                        }
                });
        return needFitNum.get() <= 0;
    }

    public static boolean giveItemToPlayerInventory(Player player, ItemStack itemStack) {
        PlayerInventory playerInventory = player.getInventory();
        if (!player.isOnline()){
            return false;
        }
        if(!canFitIntoInventory(player, itemStack)) {
            return false;
        }
        if (itemStack.getAmount() != itemStack.getMaxStackSize()) {
            playerInventory.all(itemStack.getType()).forEach((index, inventoryItem) ->
                    similarItem(inventoryItem, itemStack));
        }
        if (itemStack.getAmount() == 0) {
            return true;
        }
        int emptySlot = playerInventory.firstEmpty();
        if (emptySlot != -1) {
            playerInventory.setItem(emptySlot, itemStack);
        } else {
            log.error("玩家 {} 给予物品至背包时出现意外遗漏遗漏，物品信息（Base64）：{}", player.getName(), serializer(itemStack));
        }
        return true;
    }

    public static class Builder {
        private final Material MATERIAL;
        private Component name;
        private List<Component> lore;
        private int customModelData;
        private ItemFlag[] itemFlags;

        public Builder(Material material) {
            this.MATERIAL = material;
        }
        public Builder setComponentName(Component name) {
            this.name = name;
            return this;
        }
        public Builder setName(String name) {
            this.name = UN_ITALIC.append(AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(name)));
            return this;
        }

        public Builder setComponentLore(List<Component> lore) {
            this.lore = lore;
            return this;
        }

        public Builder setLore(List<String> lore) {
            this.lore = lore.stream().map(l -> UN_ITALIC.append(AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(l)))).collect(Collectors.toList());
            return this;
        }

        public Builder setCustomModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder setItemFlag(ItemFlag... itemFlags) {
            this.itemFlags = itemFlags;
            return this;
        }

        public ItemStack builder() {
            ItemStack itemStack = new ItemStack(MATERIAL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setCustomModelData(customModelData);
            itemMeta.displayName(name);
            itemMeta.lore(lore);
            itemMeta.addItemFlags(itemFlags);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
    }
}
