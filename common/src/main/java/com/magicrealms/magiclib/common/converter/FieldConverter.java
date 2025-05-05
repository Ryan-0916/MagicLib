package com.magicrealms.magiclib.common.converter;

/**
 * @author Ryan-0916
 * @Desc MongoDB 自定义字段转换器
 * @date 2025-05-01
 */

public interface FieldConverter<T,U> {

    U toDocumentValue(T fieldValue);

    T toFieldValue(U documentValue);

}
