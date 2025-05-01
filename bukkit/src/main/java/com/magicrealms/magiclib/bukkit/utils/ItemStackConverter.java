package com.magicrealms.magiclib.bukkit.utils;

import com.magicrealms.magiclib.common.converter.FieldConverter;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 物品转换器
 * @date 2025-05-01
 */
@SuppressWarnings("unused")
public class ItemStackConverter implements FieldConverter {

    @Override
    public String toDocumentValue(Object fieldValue) {
        return fieldValue instanceof ItemStack value ? ItemUtil.serializer(value)
                .orElse(null)
                : null;
    }

    @Override
    public ItemStack toFieldValue(Object documentValue) {
        return documentValue != null ? ItemUtil.deserializer(documentValue.toString())
                .orElse(null) : null;
    }
}
