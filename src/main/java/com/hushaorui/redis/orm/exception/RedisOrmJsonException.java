package com.hushaorui.redis.orm.exception;

/**
 * json异常
 */
public class RedisOrmJsonException extends RuntimeException {
    public RedisOrmJsonException() {
    }

    public RedisOrmJsonException(String message) {
        super(message);
    }

    public RedisOrmJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisOrmJsonException(Throwable cause) {
        super(cause);
    }

    public RedisOrmJsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
