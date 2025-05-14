package com.magicrealms.magiclib.common.annotations;

/**
 * @author Ryan-0916
 * @Desc Id 信息
 * @date 2025-05-14
 */
public record MongoId(String filedName, boolean ignoreCase) {
    public static MongoId of(String filedName, boolean ignoreCase) {
        return new MongoId(filedName, ignoreCase);
    }
}
