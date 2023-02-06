package com.hushaorui.redis.orm.common.define;

import com.hushaorui.redis.orm.exception.RedisOrmDataException;

import java.util.List;

public interface RedisOrmExecutorIF {

    /**
     * 从redis中获取对象
     * @param globalNS 全局命名空间
     * @param pojoClass 镜像，不能是接口和抽象类，必须可序列化
     * @param id 对象的id
     * @param fieldNames 想要获取的对象的字段， 若为空，则查询所有字段
     * @param <OBJ> 对象类型
     * @return 对象
     */
    <OBJ> OBJ get(String globalNS, Class<?> pojoClass, Object id, String... fieldNames) throws RedisOrmDataException;

    /**
     * 从redis中获取对象
     * @param pojoClass 镜像，不能是接口和抽象类，必须可序列化
     * @param id 对象的id
     * @param fieldNames 想要获取的对象的字段， 若为空，则查询所有字段
     * @param <OBJ> 对象类型
     * @return 对象
     */
    default <OBJ> OBJ get(Class<?> pojoClass, Object id, String... fieldNames) throws RedisOrmDataException {
        // 不使用全局命名空间
        return get("", pojoClass, id, fieldNames);
    }

    /**
     * 判断一个对象是否存在
     * @param globalNS 全局命名空间
     * @param pojoClass 镜像，不能是接口和抽象类，必须可序列化
     * @param id 对象的唯一id
     * @return 存在返回true， 不存在返回false
     * @throws RedisOrmDataException 当参数错误或连接异常时抛出此异常
     */
    boolean exist(String globalNS, Class<?> pojoClass, Object id) throws RedisOrmDataException;

    /**
     * 判断一个对象是否存在
     * @param pojoClass 镜像，不能是接口和抽象类，必须可序列化
     * @param id 对象的唯一id
     * @return 存在返回true， 不存在返回false
     * @throws RedisOrmDataException 当参数错误或连接异常时抛出此异常
     */
    default boolean exist(Class<?> pojoClass, Object id) throws RedisOrmDataException {
        return exist("", pojoClass, id);
    }

    /**
     * 从redis中获取该命名空间下的所有该类型的对象
     * @param globalNS 全局命名空间
     * @param pojoClass 镜像，不能是接口和抽象类，必须可序列化
     * @param fieldNames 想要获取的对象的字段， 若为空，则查询所有字段
     * @param <OBJ> 对象类型
     * @return 所有对象的列表
     */
    <OBJ> List<OBJ> getAll(String globalNS, Class<?> pojoClass, String... fieldNames) throws RedisOrmDataException;

    default <OBJ> List<OBJ> getAll(Class<?> pojoClass, String... fieldNames) throws RedisOrmDataException {
        return getAll("", pojoClass, fieldNames);
    }

    /**
     * 添加或更新数据
     * @param globalNS 全局命名空间
     * @param pojo 需要保存的对象
     * @param fieldNames 需要保存的字段
     */
    void put(String globalNS, Object pojo, String... fieldNames) throws RedisOrmDataException;

    /**
     * 添加或更新数据
     * @param pojo 需要保存的对象
     * @param fieldNames 需要保存的字段
     */
    default void put(Object pojo, String... fieldNames) throws RedisOrmDataException {
        // 不使用全局命名空间
        put("", pojo, fieldNames);
    }

    /**
     * 删除一个对象数据
     * @param globalNS 全局命名空间
     * @param pojoClass 镜像
     * @param id 需要删除的id
     */
    void delete(String globalNS, Class<?> pojoClass, Object id) throws RedisOrmDataException;

    /**
     * 删除一个对象数据
     * @param pojoClass 镜像
     * @param id 需要删除的id
     */
    default void delete(Class<?> pojoClass, Object id) throws RedisOrmDataException {
        delete("", pojoClass, id);
    }

    /**
     * 删除一个对象的一些字段
     * @param globalNS 全局命名空间
     * @param pojoClass 镜像
     * @param id 对象的id
     * @param fieldNames 需要删除的字段名称
     */
    void deleteField(String globalNS, Class<?> pojoClass, Object id, String... fieldNames) throws RedisOrmDataException;

    /**
     * 删除一个对象的一些字段
     * @param pojoClass 镜像
     * @param id 对象的id
     * @param fieldNames 需要删除的字段名称
     */
    default void deleteField(Class<?> pojoClass, Object id, String... fieldNames) throws RedisOrmDataException {
        deleteField("", pojoClass, id, fieldNames);
    }

    /**
     * 删除所有该命名空间下的所有对象数据
     * @param globalNS 全局命名空间
     * @param pojoClass 镜像
     */
    void deleteAll(String globalNS, Class<?> pojoClass) throws RedisOrmDataException;

    /**
     * 删除所有该命名空间下的所有对象数据
     * @param pojoClass 镜像
     */
    default void deleteAll(Class<?> pojoClass) throws RedisOrmDataException {
        deleteAll("", pojoClass);
    }

    /**
     * 清空软引用缓存
     */
    void clearSoftCache();

}
