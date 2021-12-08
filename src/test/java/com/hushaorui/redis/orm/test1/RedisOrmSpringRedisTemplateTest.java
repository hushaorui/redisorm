package com.hushaorui.redis.orm.test1;

import com.alibaba.fastjson.JSONArray;
import com.hushaorui.redis.orm.exception.RedisOrmDataException;
import com.hushaorui.redis.orm.main.RedisOrmSpringRedisTemplate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisShardInfo;

import java.util.List;

public class RedisOrmSpringRedisTemplateTest {
    private RedisOrmSpringRedisTemplate springRedisTemplate;
    @Before
    public void before() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setUsePool(false);
        JedisShardInfo jedisShardInfo = new JedisShardInfo("localhost");
        jedisShardInfo.setPassword("123456");
        factory.setShardInfo(jedisShardInfo);
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(factory);
        stringRedisTemplate.setDefaultSerializer(new StringRedisSerializer());
        springRedisTemplate = new RedisOrmSpringRedisTemplate(stringRedisTemplate);
    }

    @Test
    public void test_put_1() {
        Test1User test1User = new Test1User();
        test1User.setUsername("jack");
        test1User.setCreateTime(System.currentTimeMillis());
        test1User.setPassword("123456");
        test1User.setAdmin(true);

        Test1User test1User2 = new Test1User();
        test1User2.setUsername("tom");
        test1User2.setCreateTime(System.currentTimeMillis());
        test1User2.setPassword("343434");
        test1User2.setAdmin(false);
        try {
            springRedisTemplate.put(test1User);
            springRedisTemplate.put(test1User2);
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_1() {
        try {
            Test1User test1User = springRedisTemplate.get(Test1User.class, "jack");
            System.out.println(JSONArray.toJSONString(test1User));
            //{"admin":true,"createTime":1637832279853,"password":"123456","username":"jack"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_part_1() {
        try {
            Test1User test1User = springRedisTemplate.get(Test1User.class, "jack", "admin", "pwd");
            System.out.println(JSONArray.toJSONString(test1User));
            //{"admin":true,"password":"123456","username":"jack"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_getAll() {
        try {
            List<Test1User> all = springRedisTemplate.getAll(Test1User.class);
            System.out.println(JSONArray.toJSONString(all));
            //[{"admin":true,"createTime":1638437476648,"password":"123456","username":"jack"},{"admin":false,"createTime":1638437476648,"password":"343434","username":"tom"}]
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_put_1() {
        try {
            Test1User test1User = springRedisTemplate.get(Test1User.class, "jack");
            System.out.println(JSONArray.toJSONString(test1User));
            test1User.setPassword("22222");
            springRedisTemplate.put(test1User);
            Test1User copy = springRedisTemplate.get(Test1User.class, "jack");
            System.out.println(JSONArray.toJSONString(copy));
            //{"admin":true,"createTime":1637832279853,"password":"123456","username":"jack"}
            //{"admin":true,"createTime":1637832279853,"password":"22222","username":"jack"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_deleteField_1() {
        try {
            springRedisTemplate.deleteField(Test1User.class, "jack", "pwd");
            Test1User test1User = springRedisTemplate.get(Test1User.class, "jack");
            System.out.println(JSONArray.toJSONString(test1User));
            //{"admin":true,"createTime":1637832279853,"username":"jack"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_delete_1() {
        try {
            springRedisTemplate.delete(Test1User.class, "jack");
            Test1User test1User = springRedisTemplate.get(Test1User.class, "jack");
            System.out.println(JSONArray.toJSONString(test1User));
            //null
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_deleteAll_1() {
        try {
            springRedisTemplate.deleteAll(Test1User.class);
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    // ==================以下测试有继承关系的pojo================
    @Test
    public void test_put_2() {
        Test1Info test1Info = new Test1Info();
        test1Info.setName("李梅");
        test1Info.setEmail("123@163.com");
        test1Info.setPhone("110");
        test1Info.setAddress("shanghai");
        try {
            springRedisTemplate.put(test1Info);
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_2() {
        try {
            Test1Info test1Info = springRedisTemplate.get(Test1Info.class, "李梅");
            System.out.println(JSONArray.toJSONString(test1Info));
            //{"address":"shanghai","email":"123@163.com","name":"李梅","phone":"110"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }
}
