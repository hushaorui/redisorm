package com.hushaorui.redis.orm.test1;

import com.hushaorui.redis.orm.common.anno.RedisOrmId;
import com.hushaorui.redis.orm.common.anno.RedisOrmObj;
import com.hushaorui.redis.orm.common.anno.RedisOrmProp;

@RedisOrmObj
public class Test1Info extends Test1ParentInfo {
    @RedisOrmId
    private String name;
    private String phone;
    @RedisOrmProp(alia = "mail")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
