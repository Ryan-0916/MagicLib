package com.magicrealms.magiclib.common.converter;

/**
 * @author Ryan-0916
 * @Desc 默认转换器
 * @date 2025-05-01
 */
public class DefaultConverter implements FieldConverter<Object, Object> {

    @Override
    public Object toDocumentValue(Object fieldValue) {
        return fieldValue;
    }

    @Override
    public Object toFieldValue(Object documentValue) {
        return documentValue;
    }
    
}
