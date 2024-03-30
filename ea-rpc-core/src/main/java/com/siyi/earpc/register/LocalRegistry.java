package com.siyi.earpc.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eric
 */
public class LocalRegistry {
    public static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     */
    public static void register(String key, Class<?> implClass) {
        map.put(key, implClass);
    }

    /**
     * 获取服务
     */
    public static Class<?> get(String key) {
        return map.get(key);
    }

    /**
     * 删除服务
     */
    public static void delete(String key) {
        map.remove(key);
    }
}
