package com.hushaorui.redis.orm.main;

import com.hushaorui.redis.orm.config.RedisOrmTemplateConfig;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

/**
 * StringRedisTemplate注入的模板类
 */
public class RedisOrmSpringRedisTemplate extends RedisOrmTemplateAdapter {
    private StringRedisTemplate redisTemplate;

    public RedisOrmSpringRedisTemplate(StringRedisTemplate redisTemplate) {
        super();
        this.redisTemplate = redisTemplate;
    }

    public RedisOrmSpringRedisTemplate(StringRedisTemplate redisTemplate, RedisOrmTemplateConfig templateConfig) {
        super(templateConfig);
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void hDel(String fieldKey, String idString) {
        redisTemplate.opsForHash().delete(fieldKey, idString);
    }

    @Override
    protected void del(String fieldKey) {
        redisTemplate.delete(fieldKey);
    }

    @Override
    protected void hSet(String fieldKey, String idString, String fieldString) {
        redisTemplate.opsForHash().put(fieldKey, idString, fieldString);
    }

    @Override
    protected String hGet(String fieldKey, String idString) {
        Object value = redisTemplate.opsForHash().get(fieldKey, idString);
        if (value == null) {
            return null;
        } else {
            return String.valueOf(value);
        }
    }

    @Override
    protected Collection<String> hKeys(String fieldKey) {
        Set<Object> keys = redisTemplate.opsForHash().keys(fieldKey);
        if (keys == null) {
            return null;
        } else if (keys.isEmpty()) {
            return Collections.emptySet();
        } else {
            Set<String> set = new HashSet<>(keys.size());
            for (Object value : keys) {
                set.add(String.valueOf(value));
            }
            return set;
        }
    }

    @Override
    protected Map<String, String> hGetAll(String fieldKey) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(fieldKey);
        if (entries == null) {
            return null;
        } else if (entries.isEmpty()) {
            return Collections.emptyMap();
        } else {
            Map<String, String> map = new HashMap<>(entries.size(), 1.5f);
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                map.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
            return map;
        }
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return redisTemplate;
    }
}
