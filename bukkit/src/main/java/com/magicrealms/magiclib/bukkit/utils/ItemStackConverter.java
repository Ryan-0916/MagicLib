package com.magicrealms.magiclib.bukkit.utils;

import com.magicrealms.magiclib.common.converter.FieldConverter;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 物品转换器
 * @date 2025-05-01
 */
@SuppressWarnings("unused")
public class ItemStackConverter implements FieldConverter<ItemStack, String> {

    @Override
    public String toDocumentValue(ItemStack fieldValue) {
        return fieldValue != null ? ItemUtil.serializer(fieldValue).orElse(null)
                : null;
    }

    @Override
    public ItemStack toFieldValue(String documentValue) {
        return documentValue != null ? ItemUtil.deserializer(documentValue)
                .orElse(null) : null;
    }

}
