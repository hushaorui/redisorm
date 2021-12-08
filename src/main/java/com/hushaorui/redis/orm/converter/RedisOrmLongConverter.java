package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

/**
 * long类型解析器
 */
public class RedisOrmLongConverter implements RedisOrmConverter<Long> {
    @Override
    public Long deserialize(String stringValue, Class<?>... objClass) {
        if (stringValue == null) {
            return null;
        }
        return Long.parseLong(stringValue.trim());
    }

    @Override
    public String serialize(Long object) {
        return object == null ? null : object.toString();
    }
}
