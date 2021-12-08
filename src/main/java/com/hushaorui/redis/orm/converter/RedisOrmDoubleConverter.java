package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

/**
 * Double类型解析器
 */
public class RedisOrmDoubleConverter implements RedisOrmConverter<Double> {
    @Override
    public Double deserialize(String stringValue, Class<?>... objClass) {
        if (stringValue == null) {
            return null;
        }
        return Double.parseDouble(stringValue.trim());
    }

    @Override
    public String serialize(Double object) {
        return object == null ? null : object.toString();
    }
}
