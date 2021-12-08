package com.hushaorui.redis.orm.common.anno;

import java.lang.annotation.*;

/**
 * 标记需要持久化保存的类中的除id外的普通字段(不是必须)
 * 静态字段默认不进行保存，除非加上了该注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisOrmProp {
    /**
     * 保存数据时默认使用字段名，使用 alia可以替代字段名
     * @return 字段的别名
     */
    String alia() default "";
}
