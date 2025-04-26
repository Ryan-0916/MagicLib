package com.magicrealms.magiclib.common.utils;

import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Gson 工具里处理一些复杂情况的 Json
 * @author Ryan-0916
 * @date 2024-04-09
 */
@SuppressWarnings("unused")
@Slf4j
public class GsonUtil {
    private final static Gson gson = new Gson();

    /**
     * @param json 要解析的JSON字符串
     * @param classOf 要从JSON字符串创建的对象的类型
     * @param <T> 返回列表的类型参数
     * @return 从JSON字符串解析的指定类型的对象
     */
    public static <T> T jsonToObject(String json, Class<T> classOf) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try{
            return gson.fromJson(json, classOf);
        } catch (JsonSyntaxException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Json 转 List
     * @param json 要解析的JSON字符串列表
     * @param type 要从JSON字符串创建的对象的类型
     * @param <T> 返回列表的类型参数
     * @return 从JSON字符串解析的指定类型的对象列表
     * @throws JsonSyntaxException 如果在任何JSON字符串中存在语法错误
     */
    public static <T> List<T> jsonToList(List<String> json, Type type) throws JsonSyntaxException {
        List<T> list = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return list;
        }
        for (String oJson : json) {
            if (oJson == null) {
                continue;
            }
            list.add(gson.fromJson(oJson, type));
        }
        return list;
    }

    /**
     * 将对象转换成 Json 字符
     * @param obj 对象
     * @return 返回 Json 字符
     */
    public static String objectToJson(Object obj) {
        return gson.toJson(obj);
    }
}
