package com.magicrealms.magiclib.core.entity;

import lombok.Data;

/**
 * @author Ryan-0916
 * @Desc 文本校验器返回值
 * @date 2024-07-18
 */
@Data
public class InputValidatorResult {
    private boolean validator;
    private String message;
    public InputValidatorResult() {
        this.message = "暂无任何提示";
    }
}
