package com.hushaorui.redis.orm.common.anno;

import java.lang.annotation.*;

/**
 * 当config的useSoftCache为true，id字段默认会使用该缓存，其他字段加上此注解也会使用该缓存
 * 注意: 如果是其他程序也要进行修改的字段，不要使用此字段，否则会数据不一致
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisOrmSoftCache {
    /**
     * 保存数据时默认使用字段名，使用 alia可以替代字段名
     * @return 字段的别名
     */
    String alia() default "";
}
