package com.hushaorui.redis.orm.common.data;

import java.util.Map;

/**
 * 对redis存储对象的描述
 */
public class ClassDesc {
    /**
     * 镜像
     */
    private Class<?> clazz;
    /**
     * 类的名称(如果有别名则为别名，没有则为完整类名)
     */
    private String name;
    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 所有需要保存的字段信息，不包括id。 key : name
     */
    private Map<String, FieldDesc> propMap;
    /**
     * id字段的信息
     */
    private FieldDesc idFieldDesc;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Map<String, FieldDesc> getPropMap() {
        return propMap;
    }

    public void setPropMap(Map<String, FieldDesc> propMap) {
        this.propMap = propMap;
    }

    public FieldDesc getIdFieldDesc() {
        return idFieldDesc;
    }

    public void setIdFieldDesc(FieldDesc idFieldDesc) {
        this.idFieldDesc = idFieldDesc;
    }
}