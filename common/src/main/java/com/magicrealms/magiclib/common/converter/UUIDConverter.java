package com.magicrealms.magiclib.common.converter;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc UUID 转换器
 * @date 2025-05-01
 */
@SuppressWarnings("unused")
public class UUIDConverter implements FieldConverter{

    @Override
    public String toDocumentValue(Object fieldValue) {
        return fieldValue instanceof UUID value ? String.valueOf(value) : null;
    }

    @Override
    public UUID toFieldValue(Object documentValue) {
        return documentValue != null ? UUID.fromString(documentValue.toString())
                : null;
    }

}
