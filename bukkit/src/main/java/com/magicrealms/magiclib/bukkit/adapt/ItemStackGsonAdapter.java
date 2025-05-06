package com.magicrealms.magiclib.bukkit.adapt;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.magicrealms.magiclib.bukkit.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * @author Ryan-0916
 * @Desc Gson 物品适配器，用于将 ItemStack 对象序列化和反序列化为 JSON 格式。
 * @date 2025-05-01
 */
public class ItemStackGsonAdapter extends TypeAdapter<ItemStack> {

    /**
     * 将 ItemStack 对象序列化为 JSON 格式。
     * 如果 ItemStack 对象为 null，序列化结果将为 null。
     * 使用 ItemUtil 工具类将 ItemStack 对象转换为其 JSON 表示的字符串。
     * @param writer 目标 JsonWriter，用于写入序列化后的 JSON 数据
     * @param value 需要序列化的 ItemStack 对象
     * @throws IOException 如果写入 JSON 时发生 IO 错误
     */
    @Override
    public void write(JsonWriter writer, ItemStack value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        writer.value(ItemUtil.serializerUnClone(value).orElse(null));
    }

    /**
     * 将 JSON 数据反序列化为 ItemStack 对象。
     * 如果 JSON 数据为 null 或空值，反序列化结果将为 null。
     * 使用 ItemUtil 工具类将 JSON 字符串反序列化为 ItemStack 对象。
     * @param reader 目标 JsonReader，用于读取反序列化的 JSON 数据
     * @return 反序列化后的 ItemStack 对象，或者如果无法反序列化，则返回 null
     * @throws IOException 如果读取 JSON 时发生 IO 错误
     */
    @Override
    public ItemStack read(JsonReader reader) throws IOException {
        if (reader.peek() == null) {
            return null;
        }
        return ItemUtil.deserializer(reader.nextString()).orElse(null);
    }

}
