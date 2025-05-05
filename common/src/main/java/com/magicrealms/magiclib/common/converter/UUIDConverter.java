package com.magicrealms.magiclib.common.converter;

import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc UUID 转换器
 * @date 2025-05-01
 */
@SuppressWarnings("unused")
public class UUIDConverter implements FieldConverter<UUID, String> {

    @Override
    public String toDocumentValue(UUID fieldValue) {
        return fieldValue != null ? String.valueOf(fieldValue) : null;
    }

    @Override
    public UUID toFieldValue(String documentValue) {
        return documentValue != null ? UUID.fromString(documentValue) : null;
    }
    
}
