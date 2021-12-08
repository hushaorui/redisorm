package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

/**
 * Byte类型解析器
 */
public class RedisOrmByteConverter implements RedisOrmConverter<Byte> {
    @Override
    public Byte deserialize(String stringValue, Class<?>... objClass) {
        if (stringValue == null) {
            return null;
        }
        return Byte.parseByte(stringValue.trim());
    }

    @Override
    public String serialize(Byte object) {
        return object == null ? null : object.toString();
    }
}
