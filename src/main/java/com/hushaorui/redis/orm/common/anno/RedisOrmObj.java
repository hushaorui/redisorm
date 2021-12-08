package com.hushaorui.redis.orm.common.anno;

import java.lang.annotation.*;

/**
 * 标记需要持久化保存的类，必须要有，否则该类无法保存
 * 类中字段不能使用基本数据类型，必须是他们的包装类型
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisOrmObj {
    /**
     * 保存数据时默认使用完整类名，使用 alia可以替代完整类名
     * @return 类的别名
     */
    String alia() default "";

    /**
     * 命名空间，redisKey = 全局前缀(dataKeyPrefix) + "-" + 全局命名空间 + namespace + "-" + 完整类名
     */
    String namespace() default "";
}
