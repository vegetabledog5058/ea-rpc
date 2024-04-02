package com.siyi.earpc.register;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.siyi.earpc.config.RegistryConfig;
import com.siyi.earpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Eric
 */
public class EtcdRegistry implements Registry {
    private static final String ETCD_ROOT_PATH = "/rpc/";
    /**
     * 本机注册的节点 key 集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();
    /**
     * 注册中心服务缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();
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
        //心跳检测
        heartBeat();
    }

    /**
     * 服务注册
     *
     * @param serviceMetaInfo 服务元信息
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //获取租约客户端
        Lease leaseClient = client.getLeaseClient();
        //创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
        //将K--V关联到租约上
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        //将注册的节点key加入到本地注册的节点key集合中
        localRegisterNodeKeySet.add(registerKey);
    }

    /**
     * 服务注销
     *
     * @param serviceMetaInfo 服务元信息
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey,
                StandardCharsets.UTF_8)).get();
        localRegisterNodeKeySet.remove(registerKey);
    }

    /**
     * 服务发现
     *
     * @param serviceName 服务名称
     * @return 服务地址
     */
    @Override
    public List<ServiceMetaInfo> discovery(String serviceName) throws Exception {
        //优先从缓存中获取
        List<ServiceMetaInfo> serviceMetaInfos = registryServiceCache.readCache();
        if (serviceMetaInfos != null) {
            return serviceMetaInfos;
        }
        //从注册中心获取
        String prefix = ETCD_ROOT_PATH + serviceName + "/";
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            ByteSequence keyBytes = ByteSequence.from(prefix, StandardCharsets.UTF_8);
            List<KeyValue> kvs = kvClient.get(keyBytes, getOption).get().getKvs();
            //将json字符串转换为对象
            List<ServiceMetaInfo> serviceMetaInfoList = kvs.stream().map(keyValue -> {
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                //监听服务节点
                watch(key);
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());

            //将服务地址写入缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
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
        // 下线节点
        // 遍历本节点所有的 key
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }
        //关闭客户端，这里不能设置为null
        if (client != null) {
            client.close();
        }
        if (kvClient != null) {
            kvClient.close();
        }
    }

    /**
     * 心跳检测（服务端）
     */
    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() {

            /**
             * 执行作业
             * <p>
             * 作业的具体实现需考虑异常情况，默认情况下任务异常在监听中统一监听处理，如果不加入监听，异常会被忽略<br>
             * 因此最好自行捕获异常后处理
             */
            @Override
            public void execute() {
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        //节点已经过期,需要重新注册
                        if (keyValues.isEmpty()) {
                            continue;
                        }
                        //续约(没有过期)
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续约失败", e);
                    }
                }
            }
        });
        //支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    /**
     * 监听（消费端）
     *
     * @param serviceNodeKey  服务节点key
     */
    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        //监听的key
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), watchResponse -> {
            for (WatchEvent event : watchResponse.getEvents()) {
                switch (event.getEventType()) {
                    case DELETE:
                        //删除节点
                        registryServiceCache.clearCache();
                        System.out.println("删除节点：" + event.getKeyValue().getKey().toString(StandardCharsets.UTF_8));
                        break;
                    case PUT:
                        //TODO 添加节点
                    default:
                        break;
                }
            }
        });
    }
}
