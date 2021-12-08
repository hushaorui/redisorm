package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

/**
 * 字符类型解析器
 */
public class RedisOrmCharConverter implements RedisOrmConverter<Character> {

    @Override
    public Character deserialize(String stringValue, Class<?>... objClass) {
        if (stringValue == null) {
            return null;
        }
        if (stringValue.length() == 0) {
            return null;
        }
        return stringValue.charAt(0);
    }

    @Override
    public String serialize(Character object) {
        return object == null ? null : object.toString();
    }


}
