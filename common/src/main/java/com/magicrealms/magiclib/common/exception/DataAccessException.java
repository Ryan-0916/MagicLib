package com.magicrealms.magiclib.common.exception;

/**
 * @author Ryan-0916
 * @Desc MongoDB 自定义异常
 * @date 2025-05-06
 */
@SuppressWarnings("unused")
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {  super(message); }

    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
