package com.hushaorui.redis.orm.common.define;

/**
 * 类型转换器接口
 */
public interface RedisOrmConverter<OBJ> {
    /**
     * 将字符串解析为对应类型的对象
     * @param stringValue 原字符串
     * @return 转换后的对象
     */
    OBJ deserialize(String stringValue, Class<?>... objClasses);

    /**
     * 将对象序列化为字符串
     * @param object 原对象
     * @return 序列化后的字符串
     */
    String serialize(OBJ object);
}
