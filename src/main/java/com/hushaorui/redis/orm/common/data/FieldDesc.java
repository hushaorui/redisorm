package com.hushaorui.redis.orm.common.data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 对存储对象中的字段的描述
 */
public class FieldDesc {
    /**
     * 字段真正所属的class， 可能是父类或者父类的父类
     */
    private Class<?> owner;
    /**
     * 真正的字段
     */
    private Field field;
    /**
     * 字段是否是public
     */
    private boolean pub;
    /**
     * 保存数据时使用的名称
     */
    private String name;
    /**
     * get方法
     */
    private Method getMethod;
    /**
     * set方法
     */
    private Method setMethod;
    /**
     * 是否使用soft缓存
     */
    private boolean useSoftCache;

    /**
     * 字段是否是id
     */
    private boolean idField;

    /**
     * 字段的类型，包括泛型
     */
    private Class<?>[] fieldTypes;

    public Class<?> getOwner() {
        return owner;
    }

    public void setOwner(Class<?> owner) {
        this.owner = owner;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public boolean isPub() {
        return pub;
    }

    public void setPub(boolean pub) {
        this.pub = pub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(Method getMethod) {
        this.getMethod = getMethod;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(Method setMethod) {
        this.setMethod = setMethod;
    }

    public boolean isUseSoftCache() {
        return useSoftCache;
    }

    public void setUseSoftCache(boolean useSoftCache) {
        this.useSoftCache = useSoftCache;
    }

    public boolean isIdField() {
        return idField;
    }

    public void setIdField(boolean idField) {
        this.idField = idField;
    }

    public Class<?>[] getFieldTypes() {
        return fieldTypes;
    }

    public void setFieldTypes(Class<?>[] fieldTypes) {
        this.fieldTypes = fieldTypes;
    }
}