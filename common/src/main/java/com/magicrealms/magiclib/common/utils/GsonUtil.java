package com.magicrealms.magiclib.common.utils;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Gson 工具里处理一些复杂情况的 Json
 * @author Ryan-0916
 * @date 2024-04-09
 */
@SuppressWarnings("unused")
@Slf4j
public final class GsonUtil {

    private GsonUtil() {}

    public static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();


    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(GSON, json, clazz);
    }

    public static String toJson(Object obj) {
        return toJson(GSON, obj);
    }

    /**
     * JSON转对象
     * @param json   JSON字符串
     * @param clazz  目标类型
     * @return 解析后的对象，解析失败返回null
     */
    public static <T> T fromJson(Gson gson, String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            log.error("JSON解析失败: {}, JSON: {}", e.getMessage(), json, e);
            return null;
        }
    }

    /**
     * 对象转JSON
     * @param obj 要序列化的对象
     * @return JSON字符串，转换失败返回""
     */
    public static String toJson(Gson gson, Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return gson.toJson(obj);
        } catch (JsonIOException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            return "";
        }
    }

    public static JsonElement jsonToElement(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return JsonParser.parseString(json);
        } catch (JsonSyntaxException var2) {
            log.warn("转换Json对象时异常, 原文{}", json);
            return null;
        }
    }


}
