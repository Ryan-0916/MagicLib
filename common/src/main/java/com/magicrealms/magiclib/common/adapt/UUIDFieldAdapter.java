package com.magicrealms.magiclib.common.adapt;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Ryan-0916
 * @Desc UUID 转换器
 * 该类是 `FieldAdapter` 的一个具体实现，用于将 Java 中的 `UUID` 类型转换为 MongoDB 中的 `String` 类型，
 * 或将 MongoDB 存储的 `String` 类型转换回 `UUID` 类型。它适用于在 MongoDB 存储和读取 `UUID` 类型数据时，
 * 需要进行转换的场景。
 * @date 2025-05-01
 */
@SuppressWarnings("unused")
public class UUIDFieldAdapter extends FieldAdapter<UUID, String> {


    @Override
    public String write(UUID writer) {
        return Optional.ofNullable(writer).map(UUID::toString).orElse(null);
    }

    @Override
    public UUID read(String reader) {
        return Optional.ofNullable(reader).map(UUID::fromString).orElse(null);
    }

}
