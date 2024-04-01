package com.siyi.earpc.register;

import com.siyi.earpc.serializer.Serializer;
import com.siyi.earpc.spi.SpiLoader;

/**
 * @author Eric
 */
public class RegistryFactory {
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();
    static {
        SpiLoader.load(Registry.class);
    }
    /**
     * 获取实例
     *
     * @param key 注册中心key
     * @return 注册中心实例
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }

}
