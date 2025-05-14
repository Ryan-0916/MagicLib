package com.magicrealms.magiclib.common.utils;

import com.magicrealms.magiclib.common.adapt.DefaultFieldAdapter;
import com.magicrealms.magiclib.common.annotations.MongoField;
import com.magicrealms.magiclib.common.adapt.FieldAdapter;
import com.magicrealms.magiclib.common.annotations.MongoId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryan-0916
 * @Desc MongoDB相关工具类
 * @date 2025-05-01
 */
@Slf4j
@SuppressWarnings("unused")
public final class MongoDBUtil {

    private MongoDBUtil() {}

    private static final Map<Class<?>, List<Field>> CLASS_FIELD_CACHE =
            new ConcurrentHashMap<>();

    public static Document toDocument(Object writer) {
        return Optional.ofNullable(writer)
                .map(w -> {
                    Document document = new Document();
                    recursiveToDocument(document, w);
                    return document;
                })
                .orElse(null);
    }

    public static <T> T toObject(Document reader, Class<T> targetClass) {
        if (reader == null) { return null; }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            List<Field> fields = getCachedFields(targetClass);
            for (Field field : fields) {
                MongoField annotation = field.getAnnotation(MongoField.class);
                /* 如果获取不到注解或者注解被忽视 */
                if (annotation == null || annotation.ignore()) { continue; }
                if (annotation.recursive()) {
                    field.setAccessible(true);
                    field.set(target, toObject(reader, field.getType()));
                    continue;
                }
                String fieldName = getDocumentFieldName(field, annotation);
                if (reader.containsKey(fieldName)) {
                    Object documentValue = reader.get(fieldName);
                    Object fieldValue = read(documentValue, field, annotation);
                    if (fieldValue != null) {
                        field.setAccessible(true);
                        field.set(target, fieldValue);
                    }
                }
            }
            return target;
        } catch (Exception e) {
            throw new RuntimeException("MongoDB Document 转换成 Object 时出现未知异常", e);
        }
    }

    public static <T> Optional<MongoId> getFiledId(Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            for (Field field : getCachedFields(targetClass)) {
                MongoField annotation = field.getAnnotation(MongoField.class);
                /* 如果获取不到注解或者注解被忽视 */
                if (annotation == null || annotation.ignore()) { continue; }
                if (!annotation.id().enable()) {
                    continue;
                }
                return Optional.of(MongoId.of(getDocumentFieldName(field, annotation),
                        annotation.id().ignoreCase()));
            }
        } catch (Exception e) {
            throw new RuntimeException("获取 MongoDB 主键时出现未知异常，对象：" + targetClass, e);
        }
        return Optional.empty();
    }

    private static List<Field> getCachedFields(Class<?> clazz) {
        return CLASS_FIELD_CACHE.computeIfAbsent(clazz, k -> {
            List<Field> fields = new ArrayList<>();
            Class<?> currentClass = clazz;
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        fields.add(field);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
            return Collections.unmodifiableList(fields);
        });
    }

    private static void recursiveToDocument(Document document, Object writer) {
        List<Field> fields = getCachedFields(writer.getClass());
        for (Field field : fields) {
            try {
                MongoField annotation = field.getAnnotation(MongoField.class);
                /* 如果获取不到注解或者注解被忽视 */
                if (annotation == null || annotation.ignore()) { continue; }
                field.setAccessible(true);
                /* 获取转换后的值 */
                Object value = field.get(writer);
                /* 如果值为空不进行处理 */
                if (value == null) { continue; }
                /* 如果该对象需要穿透其内部 */
                if (annotation.recursive()) {
                    recursiveToDocument(document, value);
                    continue;
                }
                /* 将其添加至 Document 元素内部 */
                document.put(getDocumentFieldName(field, annotation),
                        write(value, field, annotation));
            } catch (IllegalAccessException e) {
                log.error("MongoDB Object 转换成 Document 时出现未知异常", e);
            }
        }
    }

    private static String getDocumentFieldName(Field field, MongoField annotation) {
        if (annotation != null && StringUtils.isNotBlank(annotation.name())) {
            return annotation.name();
        }
        /* 将驼峰式命名转换为下划线命名 */
        return camelToSnake(field.getName());
    }

    /**
     * 将驼峰式命名转换为下划线命名
     * @param camelCase 命名前
     * 例如: playerName -> player_name
     * @return 转换后
     */
    private static String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));
        for (int i = 1; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    private static Object write(Object writer, Field field, MongoField annotation) {
        try {
            return getAdapter(annotation).write(writer);
        } catch (Exception e) {
            throw new RuntimeException("MongoDB Convert To Document 时出现未知异常，字段:" + field.getName());
        }
    }

    @SuppressWarnings("unchecked")
    private static Object read(Object reader, Field field, MongoField annotation) {
        try {
            return getAdapter(annotation).read(reader);
        } catch (Exception e) {
            throw new RuntimeException("MongoDB Convert To Field 时出现未知异常，字段: " + field.getName());
        }
    }

    @SuppressWarnings("rawtypes")
    private static FieldAdapter getAdapter(MongoField annotation)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return annotation != null && annotation.adapter() != FieldAdapter.class ?
                annotation.adapter().getDeclaredConstructor().newInstance()
                : new DefaultFieldAdapter();
    }

}
