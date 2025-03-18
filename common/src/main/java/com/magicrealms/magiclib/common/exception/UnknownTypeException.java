package com.magicrealms.magiclib.common.exception;

/**
 * @author Ryan-0916
 * @Desc 未知的类型异常说明
 * @date 2024-05-06
 */
@SuppressWarnings("unused")
public class UnknownTypeException extends RuntimeException {

    public UnknownTypeException() {
        super();
    }

    public UnknownTypeException(String message) {
        super(message);
    }

    public UnknownTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownTypeException(Throwable cause) {
        super(cause);
    }

}
