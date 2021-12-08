package com.hushaorui.redis.orm.test3;

import com.hushaorui.redis.orm.common.anno.RedisOrmId;
import com.hushaorui.redis.orm.common.anno.RedisOrmObj;
import com.hushaorui.redis.orm.common.anno.RedisOrmProp;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RedisOrmObj
public class Test3User {
    @RedisOrmId
    private String username;
    @RedisOrmProp(alia = "pwd")
    private String password;
    private Long createTime;
    private boolean admin;
    private Map<String, Test3User> friends;
    private List<Integer> rankList;
    private Set<Integer> cardIdSet;
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
    public Map<String, Test3User> getFriends() {
        return friends;
    }
    public List<Integer> getRankList() {
        return rankList;
    }
    public void setRankList(List<Integer> rankList) {
        this.rankList = rankList;
    }
    public Set<Integer> getCardIdSet() {
        return cardIdSet;
    }
    public void setCardIdSet(Set<Integer> cardIdSet) {
        this.cardIdSet = cardIdSet;
    }
    public void setFriends(Map<String, Test3User> friends) {
        this.friends = friends;
    }
}
