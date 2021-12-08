package com.hushaorui.redis.orm.common.anno;

import java.lang.annotation.*;

/**
 * 标记需要持久化保存的类中的id字段，必须要有
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisOrmId {
    /**
     * 保存数据时默认使用字段名，使用 alia可以替代字段名
     * @return 字段的别名
     */
    String alia() default "";
}
