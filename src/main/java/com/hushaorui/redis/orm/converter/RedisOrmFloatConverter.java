package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

/**
 * Float类型解析器
 */
public class RedisOrmFloatConverter implements RedisOrmConverter<Float> {
    @Override
    public Float deserialize(String stringValue, Class<?>... objClass) {
        if (stringValue == null) {
            return null;
        }
        return Float.parseFloat(stringValue.trim());
    }

    @Override
    public String serialize(Float object) {
        return object == null ? null : object.toString();
    }
}
