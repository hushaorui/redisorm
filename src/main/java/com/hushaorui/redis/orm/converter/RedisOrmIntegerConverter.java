package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

/**
 * int类型解析器
 */
public class RedisOrmIntegerConverter implements RedisOrmConverter<Integer> {
    @Override
    public Integer deserialize(String stringValue, Class<?>... objClass) {
        if (stringValue == null) {
            return null;
        }
        return Integer.parseInt(stringValue.trim());
    }

    @Override
    public String serialize(Integer object) {
        return object == null ? null : object.toString();
    }
}
