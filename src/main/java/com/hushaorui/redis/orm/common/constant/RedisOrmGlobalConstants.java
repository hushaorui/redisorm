package com.hushaorui.redis.orm.common.constant;

import java.nio.charset.StandardCharsets;

/**
 * 全局性的常量
 */
public interface RedisOrmGlobalConstants {
    /**
     * 默认所有保存的数据的key都会有该前缀
     */
    String DATA_KEY_PREFIX = "ORM";

    String ENCODING = StandardCharsets.UTF_8.name();
}
