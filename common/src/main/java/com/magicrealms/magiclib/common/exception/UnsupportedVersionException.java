package com.magicrealms.magiclib.common.exception;

/**
 * @author Ryan-0916
 * @Desc 未知版本异常
 * @date 2024-05-06
 **/
@SuppressWarnings("unused")
public class UnsupportedVersionException extends RuntimeException {

    public UnsupportedVersionException() {
        super();
    }

    public UnsupportedVersionException(String message) {
        super(message);
    }

    public UnsupportedVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedVersionException(Throwable cause) {
        super(cause);
    }

}
