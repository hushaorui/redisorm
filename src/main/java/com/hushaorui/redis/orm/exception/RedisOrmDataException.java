package com.hushaorui.redis.orm.exception;

/**
 * 抛给调用者的异常
 */
public class RedisOrmDataException extends Exception {
    public RedisOrmDataException() {
    }

    public RedisOrmDataException(String message) {
        super(message);
    }

    public RedisOrmDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisOrmDataException(Throwable cause) {
        super(cause);
    }

    public RedisOrmDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
