package com.hushaorui.redis.orm.converter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hushaorui.redis.orm.common.define.RedisOrmConverter;

import java.util.*;

/**
 * 默认类型转换器解析器，默认使用fastjson，如果想要使用其他序列化协议，可自定义默认解析器
 */
public class RedisOrmFastJsonDefaultConverter implements RedisOrmConverter<Object> {

    @Override
    public Object deserialize(String stringValue, Class<?>... objClasses) {
        if (stringValue == null) {
            return null;
        }
        Class<?> fieldClass = objClasses[0];
        if (fieldClass.isArray()) {
            return handleArray(stringValue, objClasses);
        } else if (Map.class.isAssignableFrom(fieldClass)) {
            return handleMap(stringValue, objClasses);
        } else if (List.class.isAssignableFrom(fieldClass)) {
            return handleList(stringValue, objClasses);
        } else if (Set.class.isAssignableFrom(fieldClass)) {
            return handleSet(stringValue, objClasses);
        } else if (Queue.class.isAssignableFrom(fieldClass)) {
            return handleQueue(stringValue, objClasses);
        } else {
            return handleOther(stringValue, objClasses);
        }
    }

    protected Object handleArray(String stringValue, Class<?>... objClasses) {
        return JSONArray.parseArray(stringValue, objClasses);
    }

    protected Object handleOther(String stringValue, Class<?>... objClasses) {
        return JSONArray.parseObject(stringValue, objClasses[0]);
    }

    protected Object handleQueue(String stringValue, Class<?>... objClasses) {
        if (objClasses.length > 1) {
            Queue<Object> queue = (Queue<Object>) JSONArray.parseObject(stringValue, objClasses[0]);
            if (queue == null) {
                return null;
            }
            Queue<Object> newQueue = null;
            try {
                newQueue = queue.getClass().newInstance();
            } catch (Exception ignore) {}
            if (newQueue == null) {
                return queue;
            }
            for (Object object : queue) {
                if (object instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    object = jsonObject.toJavaObject(objClasses[1]);
                }
                newQueue.add(object);
            }
            return newQueue;
        } else {
            return JSONArray.parseObject(stringValue, objClasses[0]);
        }
    }
    protected Object handleSet(String stringValue, Class<?>... objClasses) {
        if (objClasses.length > 1) {
            Set<Object> set = (Set<Object>) JSONArray.parseObject(stringValue, objClasses[0]);
            if (set == null) {
                return null;
            }
            Set<Object> newSet = null;
            try {
                newSet = set.getClass().newInstance();
            } catch (Exception ignore) {}
            if (newSet == null) {
                return set;
            }
            for (Object object : set) {
                if (object instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    object = jsonObject.toJavaObject(objClasses[1]);
                }
                newSet.add(object);
            }
            return newSet;
        } else {
            return JSONArray.parseObject(stringValue, objClasses[0]);
        }
    }
    protected Object handleList(String stringValue, Class<?>... objClasses) {
        if (objClasses.length > 1) {
            return JSONArray.parseArray(stringValue, objClasses[1]);
        } else {
            // 缺了类型描述，只能使用JSONObject
            return JSONArray.parseArray(stringValue, Object.class);
        }
    }
    protected Object handleMap(String stringValue, Class<?>... objClasses) {
        Map<Object, Object> map = (Map<Object, Object>) JSONArray.parseObject(stringValue, objClasses[0]);
        if (map == null) {
            return null;
        }
        Map<Object, Object> newMap = null;
        try {
            // 尝试使用fastjson解析出来的map类型创建map
            newMap = (Map<Object, Object>) map.getClass().newInstance();
        } catch (Exception ignore) {}
        if (newMap == null) {
            // 创建失败，默认使用LinkedHashMap
            map = new LinkedHashMap<>();
        }
        if (objClasses.length < 3) {
            // 参数不足，无法继续解析
            return map;
        }
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) key;
                key = jsonObject.toJavaObject(objClasses[1]);
            }
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) value;
                value = jsonObject.toJavaObject(objClasses[2]);
            }
            newMap.put(key, value);
        }
        return newMap;
    }

    @Override
    public String serialize(Object object) {
        return JSONArray.toJSONString(object);
    }
}
