package com.hushaorui.redis.orm.config;

import com.hushaorui.redis.orm.common.constant.RedisOrmGlobalConstants;
import com.hushaorui.redis.orm.common.define.RedisOrmConverter;
import com.hushaorui.redis.orm.converter.*;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局配置
 */
public class RedisOrmTemplateConfig {
    /** 数据前缀 */
    private String dataKeyPrefix = RedisOrmGlobalConstants.DATA_KEY_PREFIX;
    /** 全局命名空间 */
    private String globalNS = "";
    /** 打印日志的流 */
    private PrintStream logPrintStream = System.out;
    /** 打印日志的级别 NONE为不打印 */
    private String logLevel = "DEBUG"; // DEBUG INFO WARN ERROR NONE
    /** 是否忽略错误的字段名称，忽略后只会打印error日志而不会直接抛出异常 */
    private boolean ignoreFieldNotFound;
    /** 类型解析器，key为需要转换的类型的全类名 */
    private Map<String, RedisOrmConverter<?>> converters = new HashMap<>();
    /** 默认类型解析器 */
    private RedisOrmConverter<Object> defaultConverter;
    /** 扫描一个或多个包，将其中有 RedisOrmObj 的类找出来，进行预加载处理，可提前发现问题 逗号切割 */
    private String scanPackageNames;

    public RedisOrmTemplateConfig() {
        // 字符串解析器
        RedisOrmConverter<String> stringConverter = new RedisOrmStringConverter();
        converters.put(String.class.getName(), stringConverter);
        // int类型解析器
        RedisOrmConverter<Integer> integerConverter = new RedisOrmIntegerConverter();
        converters.put(int.class.getName(), integerConverter);
        converters.put(Integer.class.getName(), integerConverter);
        // long 类型解析器
        RedisOrmConverter<Long> longConverter = new RedisOrmLongConverter();
        converters.put(long.class.getName(), longConverter);
        converters.put(Long.class.getName(), longConverter);
        // boolean类型解析器
        RedisOrmConverter<Boolean> booleanConverter = new RedisOrmBooleanConverter();
        converters.put(boolean.class.getName(), booleanConverter);
        converters.put(Boolean.class.getName(), booleanConverter);
        // byte类型解析器
        RedisOrmConverter<Byte> byteRedisOrmConverter = new RedisOrmByteConverter();
        converters.put(byte.class.getName(), byteRedisOrmConverter);
        converters.put(Byte.class.getName(), byteRedisOrmConverter);
        // short类型解析器
        RedisOrmConverter<Short> shortRedisOrmConverter = new RedisOrmShortConverter();
        converters.put(short.class.getName(), shortRedisOrmConverter);
        converters.put(Short.class.getName(), shortRedisOrmConverter);
        // float类型解析器
        RedisOrmConverter<Float> floatRedisOrmConverter = new RedisOrmFloatConverter();
        converters.put(float.class.getName(), floatRedisOrmConverter);
        converters.put(Float.class.getName(), floatRedisOrmConverter);
        // double类型解析器
        RedisOrmConverter<Double> doubleRedisOrmConverter = new RedisOrmDoubleConverter();
        converters.put(double.class.getName(), doubleRedisOrmConverter);
        converters.put(Double.class.getName(), doubleRedisOrmConverter);
        // BigDecimal类型解析器
        RedisOrmConverter<BigDecimal> bigDecimalRedisOrmConverter = new RedisOrmBigDecimalConverter();
        converters.put(BigDecimal.class.getName(), bigDecimalRedisOrmConverter);
        // 字符类型解析器
        RedisOrmConverter<Character> characterRedisOrmConverter = new RedisOrmCharConverter();
        converters.put(Character.class.getName(), characterRedisOrmConverter);
        converters.put(char.class.getName(), characterRedisOrmConverter);

        this.defaultConverter = new RedisOrmFastJsonDefaultConverter();

        //更多类型解析器， 可以手动添加或spring注入
    }

    public String getDataKeyPrefix() {
        return dataKeyPrefix;
    }
    public void setDataKeyPrefix(String dataKeyPrefix) {
        this.dataKeyPrefix = dataKeyPrefix;
    }
    public String getGlobalNS() {
        return globalNS;
    }
    public void setGlobalNS(String globalNS) {
        this.globalNS = globalNS;
    }
    public PrintStream getLogPrintStream() {
        return logPrintStream;
    }
    public void setLogPrintStream(PrintStream logPrintStream) {
        this.logPrintStream = logPrintStream;
    }
    public String getLogLevel() {
        return logLevel;
    }
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    public Map<String, RedisOrmConverter<?>> getConverters() {
        return converters;
    }
    public void setConverters(Map<String, RedisOrmConverter<?>> converters) {
        this.converters = converters;
    }
    public String getScanPackageNames() {
        return scanPackageNames;
    }
    public void setScanPackageNames(String scanPackageNames) {
        this.scanPackageNames = scanPackageNames;
    }
    public RedisOrmConverter<Object> getDefaultConverter() {
        return defaultConverter;
    }
    public void setDefaultConverter(RedisOrmConverter<Object> defaultConverter) {
        this.defaultConverter = defaultConverter;
    }
    public boolean isIgnoreFieldNotFound() {
        return ignoreFieldNotFound;
    }
    public void setIgnoreFieldNotFound(boolean ignoreFieldNotFound) {
        this.ignoreFieldNotFound = ignoreFieldNotFound;
    }
}
