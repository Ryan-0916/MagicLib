package com.magicrealms.magiclib.common.adapt;

/**
 * @author Ryan-0916
 * @Desc 默认转换器
 * 该类是 `FieldAdapter` 的默认实现，提供了一个无需进行任何转换的转换器。
 * 它将 Java 对象字段直接以相同的形式存储到 MongoDB 中，或者直接从 MongoDB 中读取字段而不做任何转换。
 * 此类用于默认情况下没有特殊转换需求的场景，例如字段的数据类型已经能够在 MongoDB 和 Java 中
 * 正常存储和读取时（例如 `String`, `Integer`, `Boolean` 等基本类型）。
 * `DefaultFieldAdapter` 是最简单的实现，它不对字段进行任何转换，只是原封不动地将字段返回。
 * 该类的 `write()` 和 `read()` 方法分别将字段写入和读取原样返回。
 * @date 2025-05-01
 */
public class DefaultFieldAdapter extends FieldAdapter<Object, Object> {

    @Override
    public Object write(Object writer) {
        return writer;
    }

    @Override
    public Object read(Object reader) {
        return reader;
    }
}