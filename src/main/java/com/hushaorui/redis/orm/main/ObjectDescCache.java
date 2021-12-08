package com.hushaorui.redis.orm.main;

import com.hushaorui.redis.orm.common.ReferenceBoolean;
import com.hushaorui.redis.orm.common.ReferenceString;
import com.hushaorui.redis.orm.common.anno.*;
import com.hushaorui.redis.orm.common.data.ClassDesc;
import com.hushaorui.redis.orm.common.data.FieldDesc;
import com.hushaorui.redis.orm.exception.RedisOrmJsonException;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 框架的所有缓存
 */
class ObjectDescCache {
    private static final String GET = "get";
    private static final String SET = "set";
    private static final String IS = "is";
    /**
     * 所有带有注解 RedisOrmObj 的对象描述缓存
     */
    private Map<Class<?>, ClassDesc> redisOrmObjectMap;
    /**
     * 不带有 RedisOrmObj 的对象描述缓存
     */
    private Map<Class<?>, ClassDesc> normalObjectMap;

    private static ObjectDescCache instance = new ObjectDescCache();

    private ObjectDescCache() {
        redisOrmObjectMap = new ConcurrentHashMap<>();
        normalObjectMap = new ConcurrentHashMap<>();
    }

    static ObjectDescCache getInstance() {
        return instance;
    }

    /**
     * 获取redis存储对象的描述对象，如果不存在则会创建
     *
     * @param clazz 存储对象的镜像
     * @return 描述对象
     */
    ClassDesc getRedisOrmClassDesc(Class<?> clazz) {
        return redisOrmObjectMap.computeIfAbsent(clazz, key -> newRedisObjectDesc(clazz));
    }

    public ClassDesc getClassDesc(Class<?> clazz) {
        ClassDesc classDesc = redisOrmObjectMap.get(clazz);
        if (classDesc != null) {
            return classDesc;
        }
        // 没有现成的，需要new
        RedisOrmObj redisOrmObj = clazz.getDeclaredAnnotation(RedisOrmObj.class);
        if (redisOrmObj != null) {
            // 获取有注解的
            return getRedisOrmClassDesc(clazz);
        } else {
            // 获取一个普通的
            return normalObjectMap.computeIfAbsent(clazz, key -> newNormalClassDesc(clazz));
        }
    }

    private ClassDesc newClassDesc(Class<?> clazz, boolean isRedisOrmObj) {
        RedisOrmObj redisOrmObj = clazz.getDeclaredAnnotation(RedisOrmObj.class);
        if (isRedisOrmObj && redisOrmObj == null) {
            throw new RedisOrmJsonException("No RedisOrmObj in class: " + clazz.getName());
        }
        ClassDesc classDesc = new ClassDesc();
        String classNameAlia = clazz.getName();
        String namespace = null;
        if (redisOrmObj != null) {
            if (redisOrmObj.alia().length() > 0) {
                classNameAlia = redisOrmObj.alia();
            }
            if (redisOrmObj.namespace().length() > 0) {
                namespace = redisOrmObj.namespace();
            }
        }
        classDesc.setName(classNameAlia);
        classDesc.setClazz(clazz);
        classDesc.setNamespace(namespace);

        Map<String, FieldDesc> propMap = new TreeMap<>();
        classDesc.setPropMap(propMap);

        // 获取该类的所有父类，包括自己
        List<Class<?>> parentClassList = new ArrayList<>();
        parentClassList.add(clazz);
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null && !Object.class.equals(superclass)) {
            parentClassList.add(superclass);
            superclass = superclass.getSuperclass();
        }
        Method[] methods = clazz.getMethods();
        Field[] fields = clazz.getFields();
        Set<String> finishedFieldName = new HashSet<>(methods.length);
        // 遍历所有public方法
        for (Method method : methods) {
            String methodName = method.getName();
            if ("getClass".equals(methodName)) {
                // 这里不跳过会导致递归栈溢出
                continue;
            }
            Method getMethod;
            Method setMethod;
            ReferenceString fieldName = new ReferenceString();
            if (methodName.startsWith(SET)) {
                // set方法
                if (methodName.length() <= SET.length()) {
                    // 方法名长度不够，弃用
                    continue;
                }
                if (method.getParameterCount() != 1) {
                    // set方法的参数不是一个，弃用
                    continue;
                }
                setMethod = method;
                fieldName.value = methodName.substring(SET.length(), SET.length() + 1).toLowerCase() + methodName.substring(SET.length() + 1);
                String getMethodName = GET + methodName.substring(SET.length());
                try {
                    getMethod = clazz.getMethod(getMethodName);
                } catch (NoSuchMethodException ignore) {
                    getMethod = null;
                }
                // 这里再次尝试使用 is开头
                if (getMethod == null && setMethod.getParameterTypes()[0].equals(boolean.class)) {
                    getMethodName = IS + methodName.substring(SET.length());
                    try {
                        getMethod = clazz.getMethod(getMethodName);
                    } catch (NoSuchMethodException ignore) {
                        getMethod = null;
                    }
                }
            } else if (methodName.startsWith(GET)) {
                // get方法
                if (methodName.length() <= GET.length()) {
                    continue;
                }
                if (method.getParameterCount() != 0) {
                    // set方法的参数不是0个，弃用
                    continue;
                }
                getMethod = method;
                fieldName.value = methodName.substring(GET.length(), GET.length() + 1).toLowerCase() + methodName.substring(GET.length() + 1);
                String setMethodName = SET + methodName.substring(GET.length());
                try {
                    setMethod = clazz.getMethod(setMethodName, getMethod.getReturnType());
                } catch (NoSuchMethodException ignore) {
                    setMethod = null;
                }
            } else if (methodName.startsWith(IS) && boolean.class.equals(method.getReturnType())) {
                // boolean类型专属的 get方法
                if (methodName.length() <= IS.length()) {
                    continue;
                }
                if (method.getParameterCount() != 0) {
                    // set方法的参数不是0个，弃用
                    continue;
                }
                getMethod = method;
                fieldName.value = methodName.substring(IS.length(), IS.length() + 1).toLowerCase() + methodName.substring(IS.length() + 1);
                String setMethodName = SET + methodName.substring(IS.length());
                try {
                    setMethod = clazz.getMethod(setMethodName, getMethod.getReturnType());
                } catch (NoSuchMethodException ignore) {
                    setMethod = null;
                }
            } else {
                continue;
            }
            if (finishedFieldName.contains(fieldName.value)) {
                continue;
            }
            finishedFieldName.add(fieldName.value);
            Field field = null;
            // field 所属的类
            Class<?> fieldOwner = null;
            for (Class<?> parentClass : parentClassList) {
                try {
                    field = parentClass.getDeclaredField(fieldName.value);
                } catch (NoSuchFieldException ignore) {
                }
                if (field != null) {
                    fieldOwner = parentClass;
                    break;
                }
            }
            // 该字段是否是id
            ReferenceBoolean isIdField = new ReferenceBoolean();
            // 创建字段的描述对象
            FieldDesc fieldDesc = new FieldDesc();
            if (isRedisOrmObj && field != null) {
                handleField(field, classDesc, propMap, fieldDesc, fieldName, isIdField);
            }
            fieldDesc.setName(fieldName.value);
            fieldDesc.setField(field);
            fieldDesc.setOwner(fieldOwner);
            if (getMethod != null) {
                if (isRedisOrmObj) {
                    RedisOrmIgnore redisOrmIgnore = getMethod.getDeclaredAnnotation(RedisOrmIgnore.class);
                    if (redisOrmIgnore != null) {
                        getMethod = null;
                    }
                }
            }
            if (setMethod != null) {
                if (isRedisOrmObj) {
                    RedisOrmIgnore redisOrmIgnore = setMethod.getDeclaredAnnotation(RedisOrmIgnore.class);
                    if (redisOrmIgnore != null) {
                        setMethod = null;
                    }
                }
            }
            if (isIdField.value && (getMethod == null || setMethod == null)) {
                throw new RedisOrmJsonException("Id field has no getter or setter, class: " + clazz.getName());
            }
            if (field == null) {
                if (getMethod != null) {
                    fieldDesc.setFieldTypes(new Class<?>[] {getMethod.getReturnType()});
                } else if (setMethod != null) {
                    fieldDesc.setFieldTypes(setMethod.getParameterTypes());
                }
            } else {
                handleFieldType(field, fieldDesc);
            }

            fieldDesc.setGetMethod(getMethod);
            fieldDesc.setSetMethod(setMethod);
            if (! isIdField.value) {
                // 只将非id字段放入其中
                propMap.put(fieldName.value, fieldDesc);
            }
        }
        // 遍历所有的public字段
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                // 静态或 final字段跳过
                continue;
            }
            ReferenceString fieldName = new ReferenceString();
            fieldName.value = field.getName();
            if (finishedFieldName.contains(fieldName.value)) {
                continue;
            }
            finishedFieldName.add(fieldName.value);
            // 该字段是否是id
            ReferenceBoolean isIdField = new ReferenceBoolean();

            // 创建字段的描述对象
            FieldDesc fieldDesc = new FieldDesc();
            // 该字段是public
            fieldDesc.setPub(true);
            if (isRedisOrmObj) {
                handleField(field, classDesc, propMap, fieldDesc, fieldName, isIdField);
            }
            fieldDesc.setName(fieldName.value);
            fieldDesc.setField(field);
            fieldDesc.setOwner(clazz);
            handleFieldType(field, fieldDesc);
            if (! isIdField.value) {
                // 只将非id字段放入其中
                propMap.put(fieldName.value, fieldDesc);
            }
        }
        if (isRedisOrmObj && classDesc.getIdFieldDesc() == null) {
            throw new RedisOrmJsonException("No RedisOrmId in RedisOrmObj, class: " + clazz.getName());
        }
        return classDesc;
    }

    private void handleFieldType(Field field, FieldDesc fieldDesc) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Class<?>[] fieldTypes = new Class<?>[actualTypeArguments.length + 1];
            fieldTypes[0] = field.getType();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                try {
                    fieldTypes[i + 1] = Class.forName(actualTypeArguments[i].getTypeName());
                } catch (ClassNotFoundException ignore) {}
            }
            fieldDesc.setFieldTypes(fieldTypes);
        } else {
            fieldDesc.setFieldTypes(new Class<?>[] {field.getType()});
        }
    }

    private void handleField(Field field, ClassDesc classDesc, Map<String, FieldDesc> propMap, FieldDesc fieldDesc,
                             ReferenceString fieldName, ReferenceBoolean isIdField) {
        // 字段上的忽略注解
        RedisOrmIgnore redisOrmIgnore = field.getDeclaredAnnotation(RedisOrmIgnore.class);
        if (redisOrmIgnore != null) {
            // 该字段被完全忽略
            return;
        }
        // id注解
        RedisOrmId redisOrmId = field.getDeclaredAnnotation(RedisOrmId.class);
        // 字段注解
        RedisOrmProp redisOrmProp = field.getDeclaredAnnotation(RedisOrmProp.class);
        // soft缓存注解
        RedisOrmSoftCache redisOrmSoftCache = field.getDeclaredAnnotation(RedisOrmSoftCache.class);
        // 该字段是否是id
        isIdField.value = redisOrmId != null;
        if (isIdField.value && redisOrmProp != null) {
            // 一个字段，两个注解只能用其中一个
            throw new RedisOrmJsonException("RedisOrmId and RedisOrmProp only one of them can be used int a field at the same time");
        }
        if (classDesc.getIdFieldDesc() != null && isIdField.value) {
            // 一个类，只能有一个id
            throw new RedisOrmJsonException(String.format("There are multiple RedisOrmId in class: %s", classDesc.getClazz().getName()));
        }
        if (redisOrmId != null) {
            if (redisOrmId.alia().length() > 0) {
                fieldName.value = redisOrmId.alia();
            }
        }
        if (redisOrmProp != null) {
            if (redisOrmProp.alia().length() > 0) {
                fieldName.value = redisOrmProp.alia();
                if (propMap.containsKey(fieldName.value)) {
                    throw new RedisOrmJsonException(String.format("Duplicate name: %s in class: %s", fieldName, classDesc.getClazz().getName()));
                }
            }
        }
        if (isIdField.value) {
            classDesc.setIdFieldDesc(fieldDesc);
        }
        fieldDesc.setIdField(isIdField.value);
        // id默认使用soft缓存
        fieldDesc.setUseSoftCache(isIdField.value || redisOrmSoftCache != null);

    }

    /** 创建一个非Redis数据保存类的描述对象 */
    private ClassDesc newNormalClassDesc(Class<?> clazz) {
        return newClassDesc(clazz, false);
    }

    /** 创建一个Redis数据保存类的描述对象 */
    private ClassDesc newRedisObjectDesc(Class<?> clazz) {
        return newClassDesc(clazz, true);
    }
}
