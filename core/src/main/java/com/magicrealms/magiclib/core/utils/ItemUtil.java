package com.magicrealms.magiclib.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.magicrealms.magiclib.core.adapt.ItemStackGsonAdapter;
import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /* 玩家背包最大容量 */
    public static final int MAX_STORAGE_SIZE = 36;

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
    @SuppressWarnings("deprecation")
    public static Optional<String> serializerUnClone(ItemStack itemStack) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)
        ){
            dataOutput.writeObject(itemStack);
            return Optional.of(Base64Coder.encodeLines(outputStream.toByteArray()));
        } catch (Exception exception) {
            log.error("尝试反序列化物品时出现未知异常", exception);
            return Optional.empty();
        }
    }

    /* 反序列化物品 */
    @SuppressWarnings("deprecation")
    public static Optional<ItemStack> deserializer(String deserializer) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(deserializer));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)
        ){
            return Optional.of((ItemStack) dataInput.readObject());
        } catch (Exception exception) {
            log.error("尝试反序列化物品时出现未知异常", exception);
            return Optional.empty();
        }
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
        boolean hideTooltip = configManager.getYmlValue(configPath, key + ".HideTooltip", false, ParseType.BOOLEAN);
        if (material.isEmpty()) {
            return AIR;
        }
        Builder itemBuilder = new Builder(material.get())
                .setItemFlag(itemFlags)
                .setCustomModelData(configManager.getYmlValue(configPath, key + ".ModelData", 0, ParseType.INTEGER))
                .setHideTooltip(hideTooltip);
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
        boolean hideTooltip = configManager.getYmlValue(configPath, key + ".HideTooltip", false, ParseType.BOOLEAN);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setHideTooltip(hideTooltip);
        itemMeta.setCustomModelData(configManager.getYmlValue(configPath, key + ".ModelData", 0, ParseType.INTEGER));
        nameOptional.ifPresent(name -> itemMeta.displayName(UN_ITALIC.append(AdventureHelper.deserializeComponent(
                AdventureHelper.legacyToMiniMessage(PlaceholderUtil.replacePlaceholders(name, map, player))))));
        loreOptional.ifPresent(lore ->
                itemMeta.lore(lore.stream()
                        .flatMap(line -> Arrays.stream(line.split("<newline>")))
                        .map(splitLine -> UN_ITALIC.append(
                                                AdventureHelper.deserializeComponent(
                                                        AdventureHelper.legacyToMiniMessage(
                                                                PlaceholderUtil.replacePlaceholders(splitLine, map, player)
                                                        )
                                                )
                                        )
                        ).toList()
                ));
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
            playerInventory.all(itemStack.getType())
                    .forEach((index, inventoryItem) ->
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

    /**
     * 将物品列表中的相同类型物品合并堆叠到最大堆叠数
     * @param items 要处理的物品列表
     * @return 堆叠优化后的新物品列表
     */
    public static List<ItemStack> mergeSimilarItemStacks(List<ItemStack> items) {
        Objects.requireNonNull(items);
        /* 返回物品集 */
        List<ItemStack> result = new ArrayList<>();
        /* 预处理物品集 */
        List<ItemStack> remainingItems = items.stream()
                .filter(ItemUtil::isNotAirOrNull)
                .map(ItemStack::clone).collect(Collectors
                        .toCollection(ArrayList::new));
        /* 处理物品 */
        while (!remainingItems.isEmpty()) {
            ItemStack current = remainingItems.removeFirst();
            /* 如果物品堆叠数量达到上限 */
            if (current.getAmount() == current.getMaxStackSize()) {
                result.add(current);
                continue;
            }
            /* 如果物品堆叠数量未达到上限 */
            for (int i = 0; i < remainingItems.size(); i++) {
                ItemStack other = remainingItems.get(i);
                ItemStack toMerge = current.clone();
                ItemStack formMerge = other.clone();
                /* 尝试合并 Clone 物品 */
                similarItem(toMerge, formMerge);
                /* 如果有变更 */
                if (toMerge.getAmount() > current.getAmount()) {
                    similarItem(current, other);
                    if (other.getAmount() == 0) {
                        remainingItems.remove(i);
                        i--;
                    }
                    if (current.getAmount() == current.getMaxStackSize()) {
                        break;
                    }
                }
            }
            result.add(current);
        }
        return result;
    }

    /**
     * 检查物品列表能否全部放入玩家背包
     * @param player 玩家对象
     * @param items 待检查的物品列表
     * @return 是否能全部放入背包
     */
    public static boolean canFitIntoInventory(Player player, List<ItemStack> items) {
        Objects.requireNonNull(items);
        /* 预堆叠物品列表 */
        List<ItemStack> mergedItems = mergeSimilarItemStacks(items);
        /* 预堆叠背包物品 - 排除副手与装备栏 */
        List<ItemStack> mergedInventory = mergeSimilarItemStacks(new
                ArrayList<>(Arrays.asList(player.getInventory()
                .getStorageContents())));
        /* 将两者预堆叠 */
        List<ItemStack> mergedAll = mergeSimilarItemStacks(Stream
                .concat(mergedItems.stream(), mergedInventory.stream())
                .toList());
        return mergedAll.size() <= MAX_STORAGE_SIZE;
    }

    public static class Builder {
        private final Material MATERIAL;
        private Component name;
        private List<Component> lore;
        private int customModelData;
        private ItemFlag[] itemFlags;
        private boolean hideTooltip;
        private Color color;

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
            this.lore = lore.stream()
                    .flatMap(s -> Arrays.stream(s.split("<newline>")))
                    .map(l -> UN_ITALIC.append(AdventureHelper.deserializeComponent(
                            AdventureHelper.legacyToMiniMessage(l))))
                    .toList();
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

        public Builder setHideTooltip(boolean hideTooltip) {
            this.hideTooltip = hideTooltip;
            return this;
        }

        /**
         * 设置物品颜色（支持皮革装备染色）
         * @param hex 颜色字符串，支持十六进制格式如 "#FFF" 或 "#FFFFFF"
         * @return Builder实例
         */
        public Builder setColor(String hex) {
            if (StringUtils.isBlank(hex)) {
                return this;
            }
            if (!hex.matches("^#([A-Fa-f0-9]{3}|[A-Fa-f0-9]{6})$")) {
                return this;
            }
            try {
                String hexColor = hex.substring(1);
                if (hexColor.length() == 3) {
                    hexColor = String.format("%c%c%c%c%c%c",
                            hexColor.charAt(0), hexColor.charAt(0),
                            hexColor.charAt(1), hexColor.charAt(1),
                            hexColor.charAt(2), hexColor.charAt(2));
                }
                this.color = Color.fromRGB(Integer.parseInt(hexColor, 16));
            } catch (Exception e) {
                log.warn("无法解析颜色值: {}", hex);
            }
            return this;
        }

        public ItemStack builder() {
            ItemStack itemStack = new ItemStack(MATERIAL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setCustomModelData(customModelData);
            itemMeta.displayName(name);
            itemMeta.lore(lore);
            itemMeta.addItemFlags(itemFlags);
            itemMeta.setHideTooltip(hideTooltip);
            itemStack.setItemMeta(itemMeta);
            if (color != null && itemMeta instanceof LeatherArmorMeta armorMeta) {
                armorMeta.setColor(color);
                itemStack.setItemMeta(armorMeta);
                return itemStack;
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
    }
}
