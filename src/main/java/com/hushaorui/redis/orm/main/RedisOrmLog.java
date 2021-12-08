package com.hushaorui.redis.orm.main;

import com.alibaba.fastjson.JSONArray;

import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 打印框架的日志
 */
class RedisOrmLog {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss SSS");
    private RedisOrmLogLevel currentLevel;
    private PrintStream printStream;
    RedisOrmLog(String currentLevel, PrintStream printStream) {
        this.currentLevel = RedisOrmLogLevel.get(currentLevel);
        this.printStream = printStream;
    }
    void debug(String message, Object... params) {
        if (currentLevel.getLevel() > RedisOrmLogLevel.DEBUG.getLevel()) {
            return;
        }
        if (params == null || params.length == 0) {
            printLog(RedisOrmLogLevel.DEBUG, message);
            return;
        }
        try {
            Object[] array = new String[params.length];
            for (int i = 0; i < params.length; i ++) {
                array[i] = JSONArray.toJSONString(params[i]);
            }
            printLog(RedisOrmLogLevel.DEBUG, String.format(message, array));
        } catch (NoClassDefFoundError ignore) {
            printLog(RedisOrmLogLevel.DEBUG, String.format(message, params));
        }
    }
    void info(String message) {
        if (currentLevel.getLevel() > RedisOrmLogLevel.INFO.getLevel()) {
            return;
        }
        printLog(RedisOrmLogLevel.INFO, message);
    }
    void warn(String message) {
        if (currentLevel.getLevel() > RedisOrmLogLevel.WARN.getLevel()) {
            return;
        }
        printLog(RedisOrmLogLevel.WARN, message);
    }
    void error(String message) {
        error(message, null);
    }
    void error(String message, Throwable throwable) {
        if (currentLevel.getLevel() > RedisOrmLogLevel.ERROR.getLevel()) {
            return;
        }
        printLog(RedisOrmLogLevel.ERROR, message, throwable);
    }

    private void printLog(RedisOrmLogLevel logLevel, String message) {
        printLog(logLevel, message, null);
    }
    private void printLog(RedisOrmLogLevel logLevel, String message, Throwable throwable) {
        if (printStream == null) {
            return;
        }
        String time = dateFormat.format(new Timestamp(System.currentTimeMillis()));
        printStream.println(String.format("%s [%s] [%s] %s", logLevel.getDesc(), Thread.currentThread().getName(), time, message));
        if (throwable != null) {
            throwable.printStackTrace(printStream);
        }
    }
}

enum RedisOrmLogLevel {
    DEBUG(0, "[DEBUG]"),
    INFO (1, "[INFO ]"),
    WARN (2, "[WARN ]"),
    ERROR(3, "[ERROR]"),
    NONE (4, "[NONE ]"), // 这个理论上不会打印
    ;
    private final int level;
    private final String desc;
    RedisOrmLogLevel(int level, String desc) {
        this.level = level;
        this.desc = desc;
    }
    public int getLevel() {
        return level;
    }
    public String getDesc() {
        return desc;
    }
    public static RedisOrmLogLevel get(String name) {
        try {
            return RedisOrmLogLevel.valueOf(name);
        } catch (Exception e) {
            throw  new RuntimeException("Unsupported log level: " + name, e);
        }
    }

}
