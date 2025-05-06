package com.magicrealms.magiclib.bukkit.adapt;

import com.magicrealms.magiclib.bukkit.utils.ItemUtil;
import com.magicrealms.magiclib.common.adapt.FieldAdapter;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * @author Ryan-0916
 * @Desc 物品适配器
 * 该类是 `FieldAdapter` 的具体实现，用于将 Minecraft 游戏中的 `ItemStack` 类型转换为 MongoDB 中的 `String` 类型，
 * 或将存储的 `String` 类型转换回 `ItemStack` 类型。通过这个适配器，可以方便地将 `ItemStack` 对象序列化为字符串，
 * 并将字符串反序列化为 `ItemStack` 对象，适用于在 MongoDB 中存储和读取 `ItemStack` 类型数据的场景。
 * 该适配器非常适合存储复杂的物品信息（如物品堆栈）到 MongoDB 中。由于 MongoDB 存储的是简单的基本类型（如 `String`），
 * 使用该转换器可以方便地将复杂的物品堆栈对象 `ItemStack` 转换为 MongoDB 可接受的格式，并且确保在读取时可以准确地反序列化。
 * @date 2025-05-01
 */
@SuppressWarnings("unused")
public class ItemStackFieldAdapter extends FieldAdapter<ItemStack, String> {

    /**
     * 将 ItemStack 从 Java 对象转换为 MongoDB 存储的 String 格式
     * 该方法将 `ItemStack` 对象通过序列化工具转换为字符串形式。若传入的 `ItemStack` 为 `null`，则返回 `null`。
     * @param writer Java 对象中的 `ItemStack` 字段
     * @return 转换后的 `String` 字符串表示，如果 `writer` 为 `null` 则返回 `null`
     */
    @Override
    public String write(ItemStack writer) {
        return Optional.ofNullable(writer)
                .flatMap(ItemUtil::serializer)
                .orElse(null);
    }

    /**
     * 将 MongoDB 存储的 String 格式转换为 Java 对象中的 ItemStack
     * 该方法将 MongoDB 中存储的 `String` 类型（表示 `ItemStack`）转换为 `ItemStack` 类型对象。若传入的字符串为 `null`，
     * 则返回 `null`。
     * @param reader MongoDB 存储格式中的 `String` 字段
     * @return 转换后的 `ItemStack` 对象，如果 `reader` 为 `null` 则返回 `null`
     */
    @Override
    public ItemStack read(String reader) {
        return Optional.ofNullable(reader)
                .flatMap(ItemUtil::deserializer)
                .orElse(null);
    }

}
