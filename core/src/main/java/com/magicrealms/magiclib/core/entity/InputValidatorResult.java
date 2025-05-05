package com.magicrealms.magiclib.core.entity;
/**
 * @author Ryan-0916
 * @Desc 文本校验器
 * valid 是否合法
 * message 提示
 * @date 2024-07-18
 */
@SuppressWarnings("unused")
public record InputValidatorResult(boolean valid, String message) {

    public static InputValidatorResult ofValid() {
        return new InputValidatorResult(true, null);
    }

    public static InputValidatorResult ofValid(String message) {
        return new InputValidatorResult(true, message);
    }

    public static InputValidatorResult ofInvalid(String message) {
        return new InputValidatorResult(false, message);
    }

}
