package com.hushaorui.redis.orm.main;

import com.hushaorui.redis.orm.config.RedisOrmTemplateConfig;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.Map;

/**
 * 单机版redis对象关系映射模板类
 */
public class RedisOrmAloneTemplate extends RedisOrmTemplateAdapter {

    private Jedis jedis;

    public RedisOrmAloneTemplate(Jedis jedis) {
        super();
        this.jedis = jedis;
    }

    public RedisOrmAloneTemplate(Jedis jedis, RedisOrmTemplateConfig templateConfig) {
        super(templateConfig);
        this.jedis = jedis;
    }

    @Override
    protected void hDel(String fieldKey, String idString) {
        jedis.hdel(fieldKey, idString);
    }

    @Override
    protected void del(String fieldKey) {
        jedis.del(fieldKey);
    }

    @Override
    protected void hSet(String fieldKey, String idString, String fieldString) {
        jedis.hset(fieldKey, idString, fieldString);
    }

    @Override
    protected String hGet(String fieldKey, String idString) {
        return jedis.hget(fieldKey, idString);
    }

    @Override
    protected Collection<String> hKeys(String fieldKey) {
        return jedis.hkeys(fieldKey);
    }

    @Override
    protected Map<String, String> hGetAll(String fieldKey) {
        return jedis.hgetAll(fieldKey);
    }

    public Jedis getJedis() {
        return jedis;
    }
}
