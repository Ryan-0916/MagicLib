package com.magicrealms.magiclib.bukkit.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public static final Component UN_ITALIC
            = Component.text(StringUtil.EMPTY,
            Style.style(TextDecoration.ITALIC.withState(false)));

    public static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            /* 自定义处理器 */
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .create();

    /* 序列化物品 */
    public static Optional<String> serializer(ItemStack itemStack) {
        return serializerUnClone(itemStack.clone());
    }

    /* 序列化物品 */
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

    public static ItemStack getPlayerHead(Player player) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        if (itemStack.getItemMeta() instanceof SkullMeta skull) {
            skull.setOwningPlayer(player);
            itemStack.setItemMeta(skull);
        }
        return itemStack;
    }















    public static boolean isAirOrNull(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
    }

    public static boolean isNotAirOrNull(@Nullable ItemStack itemStack) {
        return !isAirOrNull(itemStack);
    }

    public static ItemStack getItemStackByConfig(ConfigManager configManager, String configPath,
                                                 String key, ItemFlag... itemFlags) {
        return getItemStackByConfig(configManager, configPath, key, null, itemFlags);
    }

    @SuppressWarnings("DuplicatedCode")
    @NotNull
    public static ItemStack getItemStackByConfig(ConfigManager configManager, String configPath,
                                                 String key, @Nullable Map<String, String> map,
                                                 ItemFlag... itemFlags) {
        Optional<Material> material = Optional.ofNullable(Material.matchMaterial(
                configManager.getYmlValue(configPath, key + ".Mats")));
        Optional<String> name = configManager.containsYmlKey(configPath, key + ".Name") ?
                Optional.of(configManager.getYmlValue(configPath, key + ".Name")) : Optional.empty();
        Optional<List<String>> lore = configManager.containsYmlKey(configPath, key + ".Lore") ?
                configManager.getYmlListValue(configPath, key + ".Lore")  : Optional.empty();
        if (material.isEmpty()) {
            return AIR;
        }
        Builder itemBuilder = new Builder(material.get())
                .setItemFlag(itemFlags)
                .setCustomModelData(configManager.getYmlValue(configPath, key + ".ModelData", 0, ParseType.INTEGER));
        if (name.isPresent()) {
            itemBuilder = itemBuilder.setName(StringUtil.replacePlaceholders(name.get(), map));
        }
        if (lore.isPresent()) {
            itemBuilder = itemBuilder.setLore(lore.get().stream().map(l -> StringUtil.replacePlaceholders(l, map)).collect(Collectors.toList()));
        }
        return itemBuilder.builder();
    }

    @NotNull
    public static ItemStack setItemStackByConfig(ConfigManager configManager, ItemStack itemStack,
                                                 String configPath,
                                                 String key,
                                                 ItemFlag... itemFlags) {
       return setItemStackByConfig(configManager, itemStack, configPath, key, null, itemFlags);
    }

    @SuppressWarnings("DuplicatedCode")
    @NotNull
    public static ItemStack setItemStackByConfig(ConfigManager configManager, ItemStack itemStack,
                                                 String configPath,
                                                 String key, @Nullable Map<String, String> map,
                                                 ItemFlag... itemFlags) {
        Optional<String> nameOptional = configManager.containsYmlKey(configPath, key + ".Name") ?
                Optional.of(configManager.getYmlValue(configPath, key + ".Name")) : Optional.empty();
        Optional<List<String>> loreOptional = configManager.containsYmlKey(configPath, key + ".Lore") ?
                configManager.getYmlListValue(configPath, key + ".Lore") : Optional.empty();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(configManager.getYmlValue(configPath, key + ".ModelData", 0, ParseType.INTEGER));
        nameOptional.ifPresent(name -> itemMeta.displayName(UN_ITALIC.append(AdventureHelper.deserializeComponent(
                AdventureHelper.legacyToMiniMessage(StringUtil.replacePlaceholders(name, map))))));
        loreOptional.ifPresent(lore -> itemMeta.lore(lore.stream().map(l -> UN_ITALIC.append(AdventureHelper.deserializeComponent(
                AdventureHelper.legacyToMiniMessage(StringUtil.replacePlaceholders(l, map)))))
                .collect(Collectors.toList())));
        itemMeta.addItemFlags(itemFlags);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
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
