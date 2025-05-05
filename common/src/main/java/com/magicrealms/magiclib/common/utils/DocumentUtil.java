package com.magicrealms.magiclib.common.utils;

import com.magicrealms.magiclib.common.annotations.MongoField;
import com.magicrealms.magiclib.common.converter.FieldConverter;
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
public final class DocumentUtil {

    private DocumentUtil() {}

    private static final Map<Class<?>, List<Field>> CLASS_FIELD_CACHE =
            new ConcurrentHashMap<>();


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

    public static Document toDocument(Object obj) {
        if (obj == null) {
            return null;
        }
        Document document = new Document();
        /* 递归处理 Document */
        recursiveToDocument(document, obj);
        return document;
    }

    private static void recursiveToDocument(Document document, Object obj) {
        List<Field> fields = getCachedFields(obj.getClass());
        for (Field field : fields) {
            try {
                MongoField annotation = field.getAnnotation(MongoField.class);
                /* 如果获取不到注解或者注解被忽视 */
                if (annotation == null || annotation.ignore()) {
                    continue;
                }
                field.setAccessible(true);
                Object fieldValue = field.get(obj);
                if (fieldValue == null) {
                    continue;
                }
                if (annotation.recursive()) {
                    recursiveToDocument(document,fieldValue);
                    continue;
                }
                String fieldName = getDocumentFieldName(field, annotation);
                Object documentValue =
                        convertToDocumentValue(fieldValue, field, annotation);
                document.put(fieldName, documentValue);
            } catch (IllegalAccessException e) {
                log.error("反射MongoDBField时出现未知异常", e);
            }
        }
    }

    public static <T> T toObject(Document document, Class<T> targetClass) {
        if (document == null) { return null; }
        try {
            T obj = targetClass.getDeclaredConstructor().newInstance();
            List<Field> fields = getCachedFields(targetClass);
            for (Field field : fields) {
                MongoField annotation = field.getAnnotation(MongoField.class);
                /* 如果获取不到注解或者注解被忽视 */
                if (annotation == null || annotation.ignore()) {
                    continue;
                }
                if (annotation.recursive()) {
                    field.setAccessible(true);
                    field.set(obj, toObject(document, field.getType()));
                    continue;
                }
                String fieldName = getDocumentFieldName(field, annotation);
                if (document.containsKey(fieldName)) {
                    Object documentValue = document.get(fieldName);
                    Object fieldValue = convertToFieldValue(documentValue, field, annotation);
                    if (fieldValue != null) {
                        field.setAccessible(true);
                        field.set(obj, fieldValue);
                    }
                }
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Document to object", e);
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object convertToDocumentValue(Object fieldValue, Field field, MongoField annotation) {
        try {
            FieldConverter converter = getConverter(annotation);
            if (converter != null) {
                return converter.toDocumentValue(fieldValue);
            }
            return fieldValue;
        } catch (Exception e) {
            throw new RuntimeException("MongoDB Convert To Document 时出现未知异常，字段:" + field.getName());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object convertToFieldValue(Object documentValue,
                                              Field field, MongoField annotation) {
        try {
            if (documentValue == null) { return null; }
            FieldConverter converter = getConverter(annotation);
            if (converter != null) {
                return converter.toFieldValue(documentValue);
            }
            return documentValue;
        } catch (Exception e) {
            throw new RuntimeException("MongoDB Convert To Field 时出现未知异常，字段: " + field.getName());
        }
    }

    @SuppressWarnings("rawtypes")
    private static FieldConverter getConverter(MongoField annotation)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (annotation != null && annotation.converter() != FieldConverter.class) {
            return annotation.converter().getDeclaredConstructor().newInstance();
        }
        return null;
    }

}
