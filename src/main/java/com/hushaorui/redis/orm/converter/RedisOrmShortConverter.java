package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

/**
 * short类型解析器
 */
public class RedisOrmShortConverter implements RedisOrmConverter<Short> {
    @Override
    public Short deserialize(String stringValue, Class<?>... objClass) {
        if (stringValue == null) {
            return null;
        }
        return Short.parseShort(stringValue.trim());
    }

    @Override
    public String serialize(Short object) {
        return object == null ? null : object.toString();
    }
}
