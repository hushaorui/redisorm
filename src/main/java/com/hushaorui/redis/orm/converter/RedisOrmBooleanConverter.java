package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

/**
 * Boolean类型解析器
 */
public class RedisOrmBooleanConverter implements RedisOrmConverter<Boolean> {
    @Override
    public Boolean deserialize(String stringValue, Class<?>... objClass) {
        if (stringValue == null) {
            return null;
        }
        return Boolean.parseBoolean(stringValue.trim());
    }

    @Override
    public String serialize(Boolean object) {
        return object == null ? null : object.toString();
    }
}
