package com.magicrealms.magiclib.common.utils;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.manage.ConfigManage;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @Desc 物品工具类
 * @date 2024-05-27
 **/
@SuppressWarnings("unused")
public class ItemUtil {

    public static final ItemStack AIR = new ItemStack(Material.AIR);

    public static boolean isAirOrNull(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
    }

    public static boolean isNotAirOrNull(@Nullable ItemStack itemStack) {
        return !isAirOrNull(itemStack);
    }

    @NotNull
    public static Optional<String> serializerUnClone(@NotNull MagicRealmsPlugin plugin, @NotNull ItemStack itemStack) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)
        ){
            dataOutput.writeObject(itemStack);
            return Optional.of(Base64Coder.encodeLines(outputStream.toByteArray()));
        } catch (Exception exception) {
            plugin.getLoggerManager().error("尝试序列化物品时出现未知异常", exception);
            return Optional.empty();
        }
    }

    @NotNull
    public static Optional<String> serializer(@NotNull MagicRealmsPlugin plugin, @NotNull ItemStack itemStack) {
        return serializerUnClone(plugin, itemStack.clone());
    }

    @NotNull
    public static Optional<ItemStack> deserializer(@NotNull MagicRealmsPlugin plugin, @NotNull String deserializer) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(deserializer));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)
        ){
            return Optional.of((ItemStack) dataInput.readObject());
        } catch (Exception exception) {
            plugin.getLoggerManager().error("尝试反序列化物品时出现未知异常", exception);
            return Optional.empty();
        }
    }

    @NotNull
    public static ItemStack getItemStackByConfig(@NotNull ConfigManage configManage, @NotNull String configPath,
                                                 @NotNull String key, ItemFlag... itemFlags) {
        return getItemStackByConfig(configManage, configPath, key, null, itemFlags);
    }

    @NotNull
    public static ItemStack getItemStackByConfig(@NotNull ConfigManage configManage, @NotNull String configPath,
                                                 @NotNull String key, @Nullable Map<String, String> map,
                                                 ItemFlag... itemFlags) {
        Optional<Material> material = Optional.ofNullable(Material.matchMaterial(
                configManage.getYmlValue(configPath, key + ".mats")));
        Optional<String> name = configManage.containsYmlKey(configPath, key + ".name") ?
                Optional.of(configManage.getYmlValue(configPath, key + ".name")) : Optional.empty();
        Optional<List<String>> lore = configManage.getYmlListValue(configPath, key + ".lore");
        if (material.isEmpty()) {
            return AIR;
        }
        Builder itemBuilder = new Builder(material.get())
                .setItemFlag(itemFlags)
                .setCustomModelData(configManage.getYmlIntegerValue(configPath, key + ".modelData", 0));
        if (name.isPresent()) {
            itemBuilder = itemBuilder.setName(StringUtil.replacePlaceholder(name.get(), map));
        }
        if (lore.isPresent()) {
            itemBuilder = itemBuilder.setLore(lore.get().stream().map(l -> StringUtil.replacePlaceholder(l, map)).collect(Collectors.toList()));
        }
        return itemBuilder.builder();
    }

    @NotNull
    public static ItemStack setItemStackByConfig(@NotNull ConfigManage configManage, @NotNull ItemStack itemStack,
                                                 @NotNull String configPath,
                                                 @NotNull String key,
                                                 ItemFlag... itemFlags) {
       return setItemStackByConfig(configManage, itemStack, configPath, key, null, itemFlags);
    }
    @NotNull
    public static ItemStack setItemStackByConfig(@NotNull ConfigManage configManage, @NotNull ItemStack itemStack,
                                                 @NotNull String configPath,
                                                 @NotNull String key, @Nullable Map<String, String> map,
                                                 ItemFlag... itemFlags) {
        Optional<String> nameOptional = configManage.containsYmlKey(configPath, key + ".name") ?
                Optional.of(configManage.getYmlValue(configPath, key + ".name")) : Optional.empty();
        Optional<List<String>> loreOptional = configManage.getYmlListValue(configPath, key + ".lore");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(configManage.getYmlIntegerValue(configPath, key + ".modelData", 0));
        nameOptional.ifPresent(name -> itemMeta.displayName(AdventureHelper.deserializeComponent(
                AdventureHelper.legacyToMiniMessage(StringUtil.replacePlaceholder(name, map)))));
        loreOptional.ifPresent(lore -> itemMeta.lore(lore.stream().map(l -> AdventureHelper.deserializeComponent(
                AdventureHelper.legacyToMiniMessage(StringUtil.replacePlaceholder(l, map))))
                .collect(Collectors.toList())));
        itemMeta.addItemFlags(itemFlags);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void similarItem(@Nullable ItemStack to, @Nullable ItemStack from) {
        if (isAirOrNull(to) || isAirOrNull(from)) {
            return;
        }

        final int maxItemSize = to.getMaxStackSize();

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

    public static class Builder {
        private final Material MATERIAL;
        private Component name;
        private List<Component> lore;
        private int customModelData;
        private ItemFlag[] itemFlags;

        public Builder(@NotNull Material material) {
            this.MATERIAL = material;
        }
        public Builder setComponentName(@NotNull Component name) {
            this.name = name;
            return this;
        }
        public Builder setName(@NotNull String name) {
            this.name = AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(name));
            return this;
        }
        public Builder setComponentLore(@NotNull List<Component> lore) {
            this.lore = lore;
            return this;
        }

        public Builder setLore(@NotNull List<String> lore) {
            this.lore = lore.stream().map(l -> AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(l))).collect(Collectors.toList());
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
