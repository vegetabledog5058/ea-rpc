package com.siyi.earpc.register;

import cn.hutool.json.JSONUtil;
import com.siyi.earpc.config.RegistryConfig;
import com.siyi.earpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Eric
 */
public class EtcdRegistry implements Registry {
    private static final String ETCD_ROOT_PATH = "/rpc/";
    private Client client;

    private KV kvClient;

    /**
     * 服务注册
     *
     * @param registryConfig 注册中心配置
     */
    @Override
    public void init(RegistryConfig registryConfig) {
         client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
    }

    /**
     * 服务注册
     *
     * @param serviceMetaInfo 服务元信息
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        Lease leaseClient = client.getLeaseClient();
        //创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
        //将K--V关联到租约上
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
    }

    /**
     * 服务注销
     *
     * @param serviceMetaInfo 服务元信息
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(),
                StandardCharsets.UTF_8)).get();

    }

    /**
     * 服务发现
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    @Override
    public List<ServiceMetaInfo> discovery(String serviceName) throws Exception {
        String prefix = ETCD_ROOT_PATH + serviceName + "/";
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            ByteSequence key = ByteSequence.from(prefix, StandardCharsets.UTF_8);
            List<KeyValue> kvs = kvClient.get(key, getOption).get().getKvs();
            //将json字符串转换为对象
            return kvs.stream().map(keyValue -> {
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("服务获取失败");
        }
    }

    /**
     * 服务销毁
     */
    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        //关闭客户端，这里不能设置为null
        if (client != null) {
            client.close();
        }
        if (kvClient != null) {
            kvClient.close();
        }
    }
}
