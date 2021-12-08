package com.hushaorui.redis.orm.main;

import com.hushaorui.redis.orm.config.RedisOrmTemplateConfig;
import redis.clients.jedis.JedisCluster;

import java.util.Collection;
import java.util.Map;

/**
 * 集群版redis对象关系映射模板类
 */
public class RedisOrmClusterTemplate extends RedisOrmTemplateAdapter {
    // 需设置或注入
    private JedisCluster jedisCluster;

    public RedisOrmClusterTemplate(JedisCluster jedisCluster) {
        super();
        this.jedisCluster = jedisCluster;
    }

    public RedisOrmClusterTemplate(JedisCluster jedisCluster, RedisOrmTemplateConfig templateConfig) {
        super(templateConfig);
        this.jedisCluster = jedisCluster;
    }


    @Override
    protected void hDel(String fieldKey, String idString) {
        jedisCluster.hdel(fieldKey, idString);
    }

    @Override
    protected void del(String fieldKey) {
        jedisCluster.del(fieldKey);
    }

    @Override
    protected void hSet(String fieldKey, String idString, String fieldString) {
        jedisCluster.hset(fieldKey, idString, fieldString);
    }

    @Override
    protected String hGet(String fieldKey, String idString) {
        return jedisCluster.hget(fieldKey, idString);
    }

    @Override
    protected Collection<String> hKeys(String fieldKey) {
        return jedisCluster.hkeys(fieldKey);
    }

    @Override
    protected Map<String, String> hGetAll(String fieldKey) {
        return jedisCluster.hgetAll(fieldKey);
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }
}
