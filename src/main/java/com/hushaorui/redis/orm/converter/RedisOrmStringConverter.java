package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

/**
 * 字符串类型解析器
 */
public class RedisOrmStringConverter implements RedisOrmConverter<String> {

    @Override
    public String deserialize(String stringValue, Class<?>... objClass) {
        return stringValue;
    }

    @Override
    public String serialize(String object) {
        return object;
    }
}
