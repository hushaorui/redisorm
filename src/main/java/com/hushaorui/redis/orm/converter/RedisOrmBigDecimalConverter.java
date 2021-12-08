package com.hushaorui.redis.orm.converter;

import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

import java.math.BigDecimal;

/**
 * BigDecimal类型解析器
 */
public class RedisOrmBigDecimalConverter implements RedisOrmConverter<BigDecimal> {
    @Override
    public BigDecimal deserialize(String stringValue, Class<?>... objClass) {
        if (stringValue == null) {
            return null;
        }
        return new BigDecimal(stringValue.trim());
    }

    @Override
    public String serialize(BigDecimal object) {
        return object == null ? null : object.toString();
    }

}