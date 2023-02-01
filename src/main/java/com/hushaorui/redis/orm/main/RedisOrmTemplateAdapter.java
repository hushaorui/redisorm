package com.hushaorui.redis.orm.main;

import com.hushaorui.redis.orm.common.data.ClassDesc;
import com.hushaorui.redis.orm.common.data.FieldDesc;
import com.hushaorui.redis.orm.common.define.RedisOrmConverter;
import com.hushaorui.redis.orm.common.define.RedisOrmExecutorIF;
import com.hushaorui.redis.orm.config.RedisOrmTemplateConfig;
import com.hushaorui.redis.orm.exception.RedisOrmDataException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 适配模板
 */
public abstract class RedisOrmTemplateAdapter implements RedisOrmExecutorIF {
    // 全局配置
    protected RedisOrmTemplateConfig templateConfig;
    // 缓存
    protected ObjectDescCache objectDescCache;
    // 日志
    protected RedisOrmLog redisOrmLog;
    /** 默认类型解析器 */
    private RedisOrmConverter<Object> defaultConverter;
    /**
     * 所有的类型转换器
     */
    private Map<String, RedisOrmConverter<?>> converterMap = new ConcurrentHashMap<>();

    public RedisOrmTemplateAdapter() {
        this(new RedisOrmTemplateConfig());
    }

    public RedisOrmTemplateAdapter(RedisOrmTemplateConfig templateConfig) {
        if (templateConfig == null) {
            throw new NullPointerException("config cant not be null");
        }
        this.templateConfig = templateConfig;
        redisOrmLog = new RedisOrmLog(templateConfig.getLogLevel(), templateConfig.getLogPrintStream());
        converterMap.putAll(templateConfig.getConverters());
        this.defaultConverter = templateConfig.getDefaultConverter();
        objectDescCache = ObjectDescCache.getInstance();
        redisOrmLog.info(this.getClass().getName() + " initialization completed.");
    }

    @Override
    public boolean exist(String globalNS, Class<?> pojoClass, Object id) throws RedisOrmDataException {
        // 获取类描述对象
        ClassDesc classDesc = objectDescCache.getRedisOrmClassDesc(pojoClass);
        // 计算得到字段存储的key的前缀
        String fieldKey = getRedisKeyPrefix(templateConfig.getDataKeyPrefix(), globalNS, classDesc.getNamespace(), classDesc.getName())
                + classDesc.getIdFieldDesc().getName();
        // 将id转为字符串类型
        String idString = String.valueOf(id);
        // 获取id对应的值
        String idValue = hGet(fieldKey, idString);
        redisOrmLog.debug("Redis operation occurs: hGet, class:%s, fieldKey:%s, id:%s", pojoClass.getName(), fieldKey, idString);
        return idValue != null;
    }

    @Override
    public <OBJ> OBJ get(String globalNS, Class<?> pojoClass, Object id, String... fieldNames) throws RedisOrmDataException {
        ClassDesc classDesc = objectDescCache.getRedisOrmClassDesc(pojoClass);
        // 所有的字段描述集合
        Map<String, FieldDesc> propMap = classDesc.getPropMap();
        // 获取该类所存储的key的前缀
        String redisHashKeyPrefix = getRedisKeyPrefix(templateConfig.getDataKeyPrefix(), globalNS, classDesc.getNamespace(), classDesc.getName());
        // 最后返回的对象
        Object instance;
        // 所有要查询的字段都为空
        boolean allIsNull = true;
        try {
            Constructor<?> constructor = pojoClass.getConstructor();
            instance = constructor.newInstance();
            redisOrmLog.debug("Object have been created by empty argument constructor, class: %s", pojoClass.getName());
        } catch (Exception e) {
            throw new RedisOrmDataException(String.format("class: %s cant not new instance", pojoClass.getName()), e);
        }
        String idString = String.valueOf(id);
        if (fieldNames == null || fieldNames.length == 0) {
            // 先查看id字段
            boolean idIsNotNull = fillInstanceField(redisHashKeyPrefix, instance, classDesc.getIdFieldDesc(), idString);
            if (! idIsNotNull) {
                redisOrmLog.debug("globalNS:%s, class:%s, id:%s can not get object because id value is null.", globalNS, pojoClass.getName(), id);
                return null;
            }
            // 查询所有字段，直接遍历map
            for (Map.Entry<String, FieldDesc> entry : propMap.entrySet()) {
                FieldDesc fieldDesc = entry.getValue();
                boolean notNull = fillInstanceField(redisHashKeyPrefix, instance, fieldDesc, idString);
                if (notNull) {
                    allIsNull = false;
                }
            }
        } else {
            boolean notContainsId = true;
            for (String fieldName : fieldNames) {
                FieldDesc fieldDesc = propMap.get(fieldName);
                if (fieldDesc == null) {
                    if (templateConfig.isIgnoreFieldNotFound()) {
                        redisOrmLog.error(String.format("Unknown field name: %s in class: %s", fieldName, pojoClass.getName()));
                        continue;
                    } else {
                        // 找不到该字段
                        throw new RedisOrmDataException(String.format("Unknown field name: %s in class: %s", fieldName, pojoClass.getName()));
                    }
                }
                if (fieldDesc.isIdField()) {
                    notContainsId = false;
                }
                boolean notNull = fillInstanceField(redisHashKeyPrefix, instance, fieldDesc, idString);
                if (notNull) {
                    allIsNull = false;
                } else if (fieldDesc.isIdField()) {
                    // 是空的，如果是id字段，则其他数据无视
                    redisOrmLog.debug("globalNS:%s, class:%s, id:%s can not get object because id value is null.", globalNS, pojoClass.getName(), id);
                    return null;
                }
            }
            if (notContainsId) {
                // 需要查询的字段中没有id字段，我们直接将id设置到对象中
                FieldDesc idFieldDesc = classDesc.getIdFieldDesc();
                try {
                    if (idFieldDesc.isPub()) {
                        idFieldDesc.getField().set(instance, id);
                    } else {
                        idFieldDesc.getSetMethod().invoke(instance, id);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RedisOrmDataException(String.format("class:%s method:%s invoke failed", instance.getClass().getName(), idFieldDesc.getName()), e);
                }
            }
        }
        if (allIsNull) {
            // 所有字段都是null，没有必要返回一个空的instance
            redisOrmLog.debug("globalNS:%s, class:%s, id:%s can not get object because all field value is null.", globalNS, pojoClass.getName(), id);
            return null;
        }
        return (OBJ) instance;
    }

    @Override
    public <OBJ> List<OBJ> getAll(String globalNS, Class<?> pojoClass, String... fieldNames) throws RedisOrmDataException {
        ClassDesc classDesc = objectDescCache.getRedisOrmClassDesc(pojoClass);
        // 获取该类所存储的key的前缀
        String redisHashKeyPrefix = getRedisKeyPrefix(templateConfig.getDataKeyPrefix(), globalNS, classDesc.getNamespace(), classDesc.getName());
        // id字段的key
        String idFieldKey = redisHashKeyPrefix + classDesc.getIdFieldDesc().getName();
        // 查找到所有的id
        Collection<String> idCollection = hKeys(idFieldKey);
        redisOrmLog.debug("Redis operation occurs: hKeys, class:%s, id:%s", pojoClass.getName(), idFieldKey);
        // 最后返回的对象集合
        List<OBJ> instanceList = new ArrayList<>();
        for (String idString : idCollection) {
            try {
                OBJ obj = get(globalNS, pojoClass, idString, fieldNames);
                if (obj != null) {
                    instanceList.add(obj);
                }
            } catch (Exception e) {
                /*// 这里打印警告日志
                redisOrmLog.error(String.format("globalNS:%s, class:%s, id:%s get object error, exception:%s.",
                        globalNS, pojoClass.getName(), idString, e.getLocalizedMessage()));*/
                throw new RedisOrmDataException(String.format("globalNS:%s, class:%s, id:%s get object error, exception:%s.",
                        globalNS, pojoClass.getName(), idString, e.toString()));
            }
        }
        return instanceList;
    }

    @Override
    public void put(String globalNS, Object pojo, String... fieldNames) throws RedisOrmDataException {
        if (pojo == null) {
            return;
        }
        Class<?> pojoClass = pojo.getClass();
        ClassDesc classDesc = objectDescCache.getRedisOrmClassDesc(pojoClass);
        // 所有的字段描述集合
        Map<String, FieldDesc> propMap = classDesc.getPropMap();
        FieldDesc idFieldDesc = classDesc.getIdFieldDesc();
        String idString = getStringValueFromInstance(pojo, idFieldDesc);
        if (idString == null || idString.isEmpty()) {
            // 无法保存id字段为空的对象
            throw new RedisOrmDataException(String.format("cant not put instance when id is null in class: %s", pojoClass.getName()));
        }
        // 获取该类所存储的key的前缀
        String redisHashKeyPrefix = getRedisKeyPrefix(templateConfig.getDataKeyPrefix(), globalNS, classDesc.getNamespace(), classDesc.getName());
        // 已经进行存储的字段
        Set<FieldDesc> savedSet = new HashSet<>();
        // 存储时发生了错误
        RedisOrmDataException exception = null;
        if (fieldNames == null || fieldNames.length == 0) {
            // 先保存id字段
            try {
                saveInstanceField(redisHashKeyPrefix, pojo, idFieldDesc, idString);
                savedSet.add(idFieldDesc);
            } catch (RedisOrmDataException e) {
                exception = e;
            }
            if (exception == null) {
                // 存储所有非null字段
                for (Map.Entry<String, FieldDesc> entry : propMap.entrySet()) {
                    FieldDesc fieldDesc = entry.getValue();
                    try {
                        saveInstanceField(redisHashKeyPrefix, pojo, fieldDesc, idString);
                        savedSet.add(entry.getValue());
                    } catch (RedisOrmDataException e) {
                        exception = e;
                        break;
                    }
                }
            }
        } else {
            // 存储指定的字段
            for (String fieldName : fieldNames) {
                FieldDesc fieldDesc;
                if (idFieldDesc.getName().equals(fieldName)) {
                    fieldDesc = idFieldDesc;
                } else {
                    fieldDesc = propMap.get(fieldName);
                }
                if (fieldDesc == null) {
                    // 找不到该字段
                    throw new RedisOrmDataException(String.format("Unknown field name: %s in class: %s", fieldName, pojoClass.getName()));
                }
                try {
                    saveInstanceField(redisHashKeyPrefix, pojo, fieldDesc, idString);
                    savedSet.add(idFieldDesc);
                } catch (RedisOrmDataException e) {
                    exception = e;
                    break;
                }
            }
        }
        if (exception != null) {
            // 回滚，将已经存储的字段删除
            for (FieldDesc saved : savedSet) {
                try {
                    deleteField(globalNS, pojoClass, idString, saved.getName());
                } catch (Exception e) {
                    redisOrmLog.error(String.format("delete field failed, globalNS:%s, class:%s, id:%s,field:%s",
                            globalNS, pojoClass, idString, saved.getName()), e);
                }
            }
            throw exception;
        }
    }

    @Override
    public void delete(String globalNS, Class<?> pojoClass, Object id) throws RedisOrmDataException {
        ClassDesc classDesc = objectDescCache.getRedisOrmClassDesc(pojoClass);
        // 所有的字段描述集合
        Map<String, FieldDesc> propMap = classDesc.getPropMap();
        String idString = id.toString();
        // 获取该类所存储的key的前缀
        String redisHashKeyPrefix = getRedisKeyPrefix(templateConfig.getDataKeyPrefix(), globalNS, classDesc.getNamespace(), classDesc.getName());
        // 先删除id字段
        String idFieldKey = redisHashKeyPrefix + classDesc.getIdFieldDesc().getName();
        hDel(idFieldKey, idString);
        redisOrmLog.debug("Redis operation occurs: hDel, class:%s, fieldKey:%s, id:%s", pojoClass.getName(), idFieldKey, idString);

        // 再删除其他字段
        for (FieldDesc fieldDesc : propMap.values()) {
            String fieldKey = redisHashKeyPrefix + fieldDesc.getName();
            hDel(fieldKey, idString);
            redisOrmLog.debug("Redis operation occurs: hDel, class:%s, fieldKey:%s, id:%s", pojoClass.getName(), fieldKey, idString);
        }
    }

    @Override
    public void deleteField(String globalNS, Class<?> pojoClass, Object id, String... fieldNames) throws RedisOrmDataException {
        if (fieldNames == null || fieldNames.length == 0) {
            // 没有可删除的字段
            throw new RedisOrmDataException("No field can be delete is class: " + pojoClass.getName());
        }
        ClassDesc classDesc = objectDescCache.getRedisOrmClassDesc(pojoClass);
        // 所有的字段描述集合
        Map<String, FieldDesc> propMap = classDesc.getPropMap();
        String idString = id.toString();
        // 获取该类所存储的key的前缀
        String redisHashKeyPrefix = getRedisKeyPrefix(templateConfig.getDataKeyPrefix(), globalNS, classDesc.getNamespace(), classDesc.getName());
        if (! templateConfig.isIgnoreFieldNotFound()) {
            // 先检查一遍
            for (String fieldName : fieldNames) {
                FieldDesc fieldDesc = propMap.get(fieldName);
                if (fieldDesc == null) {
                    throw new RedisOrmDataException(String.format("Unknown field name: %s in class: %s", fieldName, pojoClass.getName()));
                }
            }
        }

        // 删除指定的字段
        for (String fieldName : fieldNames) {
            FieldDesc fieldDesc = propMap.get(fieldName);
            String fieldKey = redisHashKeyPrefix + fieldDesc.getName();
            hDel(fieldKey, idString);
            redisOrmLog.debug("Redis operation occurs: hDel, class:%s, fieldKey:%s, id:%s", pojoClass.getName(), fieldKey, idString);
        }
    }

    @Override
    public void deleteAll(String globalNS, Class<?> pojoClass) throws RedisOrmDataException {
        ClassDesc classDesc = objectDescCache.getRedisOrmClassDesc(pojoClass);
        // 获取该类所存储的key的前缀
        String redisHashKeyPrefix = getRedisKeyPrefix(templateConfig.getDataKeyPrefix(), globalNS, classDesc.getNamespace(), classDesc.getName());
        // 先删除id字段
        String idFieldKey = redisHashKeyPrefix + classDesc.getIdFieldDesc().getName();
        // 直接将整个hash删除
        del(idFieldKey);
        redisOrmLog.debug("Redis operation occurs: del, class:%s, fieldKey:%s", pojoClass.getName(), idFieldKey);

        // 所有的非id字段描述集合
        Map<String, FieldDesc> propMap = classDesc.getPropMap();
        for (FieldDesc fieldDesc : propMap.values()) {
            String fieldKey = redisHashKeyPrefix + fieldDesc.getName();
            // 直接将整个hash删除
            del(fieldKey);
            redisOrmLog.debug("Redis operation occurs: del, class:%s, fieldKey:%s", pojoClass.getName(), fieldKey);
        }
    }


    /**
     * 将字符串解析为对应类型的对象
     */
    private Object parseStringToObject(String fieldValueString, FieldDesc fieldDesc) {
        // 字段的类型，如果存在泛型，则数组长度大于1
        Class<?>[] fieldTypes = fieldDesc.getFieldTypes();
        // 获取对应的类型解析器
        RedisOrmConverter redisOrmConverter = converterMap.get(fieldTypes[0].getName());
        if (redisOrmConverter == null) {
            if (defaultConverter != null) {
                return defaultConverter.deserialize(fieldValueString, fieldTypes);
            } else {
                redisOrmLog.error("defaultConverter is null, can not handle class: " + fieldTypes[0].getName());
                return null;
            }
        }
        return redisOrmConverter.deserialize(fieldValueString, fieldTypes);
    }

    /** 从对象中获取对应字段的值转化后的字符串 */
    private String getStringValueFromInstance(Object instance, FieldDesc fieldDesc) throws RedisOrmDataException {
        Method getMethod = fieldDesc.getGetMethod();
        Object value;
        Class<?> fieldType;
        if (getMethod == null) {
            if (fieldDesc.isPub()) {
                // 字段是public，可以直接获取值
                try {
                    fieldType = fieldDesc.getField().getType();
                    value = fieldDesc.getField().get(instance);
                } catch (IllegalAccessException e) {
                    throw new RedisOrmDataException(String.format("class: %s field: %s get failed", instance.getClass().getName(), fieldDesc.getField().getName()), e);
                }
            } else {
                return null;
            }
        } else {
            try {
                fieldType = getMethod.getReturnType();
                value = getMethod.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RedisOrmDataException(String.format("class: %s method: %s invoke failed", instance.getClass().getName(), getMethod.getName()), e);
            }
        }

        if (value == null) {
            return null;
        }
        RedisOrmConverter redisOrmConverter = converterMap.get(fieldType.getName());
        if (redisOrmConverter == null) {
            if (defaultConverter != null) {
                return defaultConverter.serialize(value);
            } else {
                // 没有找到对应的转换器，直接toString方法
                return value.toString();
            }
        } else {
            return redisOrmConverter.serialize(value);
        }
    }

    private void saveInstanceField(String redisHashKeyPrefix, Object instance, FieldDesc fieldDesc, String idString) throws RedisOrmDataException {
        String fieldKey = redisHashKeyPrefix + fieldDesc.getName();
        String fieldString;
        if (fieldDesc.isIdField()) {
            fieldString = idString;
        } else {
            fieldString = getStringValueFromInstance(instance, fieldDesc);
        }
        if (fieldString == null) {
            // null不进行存储
            return;
        }
        hSet(fieldKey, idString, fieldString);
        redisOrmLog.debug("Redis operation occurs: hSet, class:%s, fieldKey:%s, id:%s, value:%s",
                instance.getClass().getName(), fieldKey, idString, fieldString);
    }

    /** 查询数据，填充到instance中 */
    private boolean fillInstanceField(String redisHashKeyPrefix, Object instance, FieldDesc fieldDesc, String idString) throws RedisOrmDataException {
        String fieldKey = redisHashKeyPrefix + fieldDesc.getName();
        // 该字段序列化为字符串后的值
        String fieldValueString = hGet(fieldKey, idString);
        redisOrmLog.debug("Redis operation occurs: hGet, class:%s, fieldKey:%s, id:%s", instance.getClass().getName(), fieldKey, idString);
        Class<?> instanceClass = instance.getClass();
        Object fieldValue = parseStringToObject(fieldValueString, fieldDesc);
        if (fieldValue == null) {
            return false;
        }
        Method setMethod = fieldDesc.getSetMethod();
        try {
            if (setMethod == null) {
                if (fieldDesc.isPub()) {
                    // 字段是public，直接设值
                    fieldDesc.getField().set(instance, fieldValue);
                } else {
                    return false;
                }
            } else {
                setMethod.invoke(instance, fieldValue);
            }
        } catch (IllegalAccessException |InvocationTargetException e) {
            throw new RedisOrmDataException(String.format("class: %s field:%s set value failed", instanceClass, fieldDesc.getName()), e);
        }
        return true;
    }

    private String getRedisKeyPrefix(String dataKeyPrefix, String globalNS, String namespace, String classNameAlia) {
        StringBuilder builder = new StringBuilder();
        if (dataKeyPrefix != null && dataKeyPrefix.length() > 0) {
            builder.append(dataKeyPrefix).append("-");
        }
        if (globalNS != null && globalNS.length() > 0) {
            builder.append(globalNS).append("-");
        }
        if (namespace != null && namespace.length() > 0) {
            builder.append(namespace).append("-");
        }
        builder.append(classNameAlia).append("-");
        return builder.toString();
    }

    /** 删除hash中的一个元素 */
    protected abstract void hDel(String fieldKey, String idString);
    /** 删除整个hash */
    protected abstract void del(String fieldKey);
    /** 在hash中设置一个元素 */
    protected abstract void hSet(String fieldKey, String idString, String fieldString);
    /** 在hash中获取一个元素的值 */
    protected abstract String hGet(String fieldKey, String idString);
    /** 获取hash中的所有key */
    protected abstract Collection<String> hKeys(String fieldKey);
    protected abstract Map<String, String> hGetAll(String fieldKey);
}
