package com.hushaorui.redis.orm.common.anno;

import java.lang.annotation.*;

/**
 * 标记需要忽略的方法或字段
 * 1, 标记字段，则该字段以及对应的get set方法都会被忽略
 * 2, 标记get方法，则该字段不会在保存数据时读取
 * 3, 标记set方法，则该字段不会在从redis读取数据时设值
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisOrmIgnore {
}
