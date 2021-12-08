package com.hushaorui.redis.orm.test1;

import com.hushaorui.redis.orm.common.anno.RedisOrmId;
import com.hushaorui.redis.orm.common.anno.RedisOrmObj;
import com.hushaorui.redis.orm.common.anno.RedisOrmProp;

import java.util.Map;

@RedisOrmObj
public class Test1User {
    @RedisOrmId
    private String username;
    @RedisOrmProp(alia = "pwd")
    private String password;
    private Long createTime;
    private boolean admin;
    private Map<String, Test1User> friends;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    public boolean isAdmin() {
        return admin;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    public Map<String, Test1User> getFriends() {
        return friends;
    }
    public void setFriends(Map<String, Test1User> friends) {
        this.friends = friends;
    }
}
