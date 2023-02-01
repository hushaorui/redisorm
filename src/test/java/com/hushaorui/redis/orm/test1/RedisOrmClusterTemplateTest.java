package com.hushaorui.redis.orm.test1;

import com.alibaba.fastjson.JSONArray;
import com.hushaorui.redis.orm.exception.RedisOrmDataException;
import com.hushaorui.redis.orm.main.RedisOrmClusterTemplate;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedisOrmClusterTemplateTest {
    private RedisOrmClusterTemplate clusterTemplate;
    @Before
    public void before() {
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        hostAndPorts.add(new HostAndPort("192.168.1.254", 7000));
        hostAndPorts.add(new HostAndPort("192.168.1.254", 7001));
        hostAndPorts.add(new HostAndPort("192.168.1.254", 7002));
        hostAndPorts.add(new HostAndPort("192.168.1.254", 7003));
        int connectionTimeout = 2000;
        int soTimeout = 2000;
        int maxAttempts = 5;
        String password = "Morlia3659";
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        JedisCluster jedisCluster = new JedisCluster(hostAndPorts, connectionTimeout, soTimeout, maxAttempts, password, genericObjectPoolConfig);
        clusterTemplate = new RedisOrmClusterTemplate(jedisCluster);
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
            clusterTemplate.put(test1User);
            clusterTemplate.put(test1User2);
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_1() {
        try {
            Test1User test1User = clusterTemplate.get(Test1User.class, "jack");
            System.out.println(JSONArray.toJSONString(test1User));
            //{"admin":true,"createTime":1637832279853,"password":"123456","username":"jack"}
            Test1User user2 = clusterTemplate.get(Test1User.class, "jack", "admin");
            System.out.println(JSONArray.toJSONString(user2));
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_part_1() {
        try {
            Test1User test1User = clusterTemplate.get(Test1User.class, "jack", "admin", "pwd");
            System.out.println(JSONArray.toJSONString(test1User));
            //{"admin":true,"password":"123456","username":"jack"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_getAll() {
        try {
            List<Object> all = clusterTemplate.getAll(Test1User.class);
            System.out.println(JSONArray.toJSONString(all));
            //[{"admin":true,"createTime":1637832279853,"password":"123456","username":"jack"},{"admin":false,"createTime":1637832279853,"password":"343434","username":"tom"}]
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_put_1() {
        try {
            Test1User test1User = clusterTemplate.get(Test1User.class, "jack");
            System.out.println(JSONArray.toJSONString(test1User));
            test1User.setPassword("22222");
            clusterTemplate.put(test1User);
            Test1User copy = clusterTemplate.get(Test1User.class, "jack");
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
            clusterTemplate.deleteField(Test1User.class, "jack", "pwd");
            Test1User test1User = clusterTemplate.get(Test1User.class, "jack");
            System.out.println(JSONArray.toJSONString(test1User));
            //{"admin":true,"createTime":1637832279853,"username":"jack"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_delete_1() {
        try {
            clusterTemplate.delete(Test1User.class, "jack");
            Test1User test1User = clusterTemplate.get(Test1User.class, "jack");
            System.out.println(JSONArray.toJSONString(test1User));
            //null
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_deleteAll_1() {
        try {
            clusterTemplate.deleteAll(Test1User.class);
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
            clusterTemplate.put(test1Info);
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_get_2() {
        try {
            Test1Info test1Info = clusterTemplate.get(Test1Info.class, "李梅");
            System.out.println(JSONArray.toJSONString(test1Info));
            //{"address":"shanghai","email":"123@163.com","name":"李梅","phone":"110"}
        } catch (RedisOrmDataException e) {
            e.printStackTrace();
        }
    }

}
