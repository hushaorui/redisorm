package com.hushaorui.redis.orm.test3;

import com.alibaba.fastjson.JSONArray;
import com.hushaorui.redis.orm.exception.RedisOrmDataException;
import com.hushaorui.redis.orm.main.RedisOrmAloneTemplate;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

import java.util.*;

public class RedisOrmAloneTemplateTest3 {
    private RedisOrmAloneTemplate aloneTemplate;

    @Before
    public void before() {
        JedisShardInfo jedisShardInfo = new JedisShardInfo("127.0.0.1");
        jedisShardInfo.setPassword("123456");
        Jedis jedis = new Jedis(jedisShardInfo);
        aloneTemplate = new RedisOrmAloneTemplate(jedis);
    }

    @Test
    public void test_put_1() {
        Test3User test3User = new Test3User();
        test3User.setUsername("jack");
        test3User.setCreateTime(System.currentTimeMillis());
        test3User.setPassword("123456");
        test3User.setAdmin(true);

        Test3User test2User3 = new Test3User();
        test2User3.setUsername("tom");
        test2User3.setCreateTime(System.currentTimeMillis());
        test2User3.setPassword("343434");
        test2User3.setAdmin(false);

        Map<String, Test3User> friends1 = new HashMap<>();
        Map<String, Test3User> friends2 = new HashMap<>();
        test3User.setFriends(friends1);
        friends1.put(test2User3.getUsername(), test2User3);
        test2User3.setFriends(friends2);
        friends2.put(test3User.getUsername(), test3User);

        List<Integer> rankList = new ArrayList<>();
        rankList.add(1);
        rankList.add(5);
        test3User.setRankList(rankList);
        test2User3.setRankList(rankList);

        Set<Integer> cardIdSet = new HashSet<>();
        cardIdSet.add(1123124);
        cardIdSet.add(112312345);
        cardIdSet.add(1123235);
        test3User.setCardIdSet(cardIdSet);

        try {
            aloneTemplate.put(test3User);
            aloneTemplate.put(test2User3);
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注意！上述情况会导致 java.lang.StackOverflowError
     */
    @Test
    public void test_get_1() {
        try {
            Test3User test3User = aloneTemplate.get(Test3User.class, "jack");
            System.out.println(JSONArray.toJSONString(test3User));
            //{"admin":true,"createTime":1637832279853,"password":"123456","username":"jack"}
        } catch (RedisOrmDataException | StackOverflowError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_part_1() {
        try {
            Test3User test3User = aloneTemplate.get(Test3User.class, "jack", "admin", "pwd");
            System.out.println(JSONArray.toJSONString(test3User));
            //{"admin":true,"password":"123456","username":"jack"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_getAll() {
        try {
            List<Object> all = aloneTemplate.getAll(Test3User.class);
            System.out.println(JSONArray.toJSONString(all));
            //[{"admin":true,"createTime":1638437476648,"password":"123456","username":"jack"},{"admin":false,"createTime":1638437476648,"password":"343434","username":"tom"}]
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_put_1() {
        try {
            Test3User test3User = aloneTemplate.get(Test3User.class, "jack");
            System.out.println(JSONArray.toJSONString(test3User));
            test3User.setPassword("22222");
            aloneTemplate.put(test3User);
            Test3User copy = aloneTemplate.get(Test3User.class, "jack");
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
            aloneTemplate.deleteField(Test3User.class, "jack", "pwd");
            Test3User test3User = aloneTemplate.get(Test3User.class, "jack");
            System.out.println(JSONArray.toJSONString(test3User));
            //{"admin":true,"createTime":1637832279853,"username":"jack"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_delete_1() {
        try {
            aloneTemplate.delete(Test3User.class, "jack");
            Test3User test3User = aloneTemplate.get(Test3User.class, "jack");
            System.out.println(JSONArray.toJSONString(test3User));
            //null
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_deleteAll_1() {
        try {
            aloneTemplate.deleteAll(Test3User.class);
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

}
