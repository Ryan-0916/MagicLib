package com.magicrealms.magiclib.common.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Gson 工具里处理一些复杂情况的 Json
 * @author Ryan-0916
 * @date 2024-04-09
 */
@SuppressWarnings("unused")
@Slf4j
public final class GsonUtil {

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").disableHtmlEscaping().create();

    /**
     * JSON转对象
     * @param json   JSON字符串
     * @param clazz  目标类型
     * @return 解析后的对象，解析失败返回null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return GSON.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            log.error("JSON解析失败: {}, JSON: {}", e.getMessage(), json, e);
            return null;
        }
    }

    /**
     * JSON转对象（支持复杂类型）
     * @param json  JSON字符串
     * @param type  目标类型（使用TypeToken构造）
     * @return 解析后的对象，解析失败返回null
     */
    public static <T> T fromJson(String json, Type type) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return GSON.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            log.error("JSON解析失败: {}, JSON: {}", e.getMessage(), json, e);
            return null;
        }
    }

    /**
     * JSON 转 List
     * @param json  JSON字符串
     * @param clazz 列表元素类型
     * @return 解析后的List，解析失败返回空List
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        try {
            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            return GSON.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            log.error("JSON解析失败: {}, JSON: {}", e.getMessage(), json, e);
            return Collections.emptyList();
        }
    }

    /**
     * 对象转JSON
     * @param obj 要序列化的对象
     * @return JSON字符串，转换失败返回""
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return GSON.toJson(obj);
        } catch (JsonIOException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * 对象转JSON（美化输出）
     * @param obj 要序列化的对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(obj);
        } catch (JsonIOException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * 安全地将多个JSON字符串转换为对象列表
     * @param jsonList JSON字符串列表
     * @param clazz    目标类型
     * @return 成功解析的对象列表（跳过失败项）
     */
    public static <T> List<T> safeConvertList(Collection<String> jsonList, Class<T> clazz) {
        if (jsonList == null || jsonList.isEmpty()) {
            return Collections.emptyList();
        }

        return jsonList.stream()
                .filter(Objects::nonNull)
                .map(json -> fromJson(json, clazz))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 判断字符串是否为有效JSON
     * @param json 待检查字符串
     * @return 是否为有效JSON
     */
    public static boolean isValidJson(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }
        try {
            JsonParser.parseString(json);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }
}
