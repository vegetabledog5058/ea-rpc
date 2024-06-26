package com.siyi.earpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.siyi.earpc.RpcApplication;
import com.siyi.earpc.config.RegistryConfig;
import com.siyi.earpc.config.RpcConfig;
import com.siyi.earpc.constant.RpcConstant;
import com.siyi.earpc.fault.retry.RetryStrategy;
import com.siyi.earpc.fault.retry.RetryStrategyFactory;
import com.siyi.earpc.fault.tolerant.TolerantStrategy;
import com.siyi.earpc.fault.tolerant.TolerantStrategyFactory;
import com.siyi.earpc.loadbalancer.LoadBalancer;
import com.siyi.earpc.loadbalancer.LoadBalancerFactory;
import com.siyi.earpc.model.RpcRequest;
import com.siyi.earpc.model.RpcResponse;
import com.siyi.earpc.model.ServiceMetaInfo;
import com.siyi.earpc.protocol.*;
import com.siyi.earpc.register.Registry;
import com.siyi.earpc.register.RegistryFactory;
import com.siyi.earpc.serializer.Serializer;
import com.siyi.earpc.serializer.SerializerFactory;
import com.siyi.earpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        //发送请求
        RpcRequest rpcRequest = RpcRequest.builder().name(method.getDeclaringClass().getName()).methodName(method.getName()).args(args).parameterTypes(method.getParameterTypes()).build();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result;
            // 依靠注册中心解决硬编码问题
            String serviceName = method.getDeclaringClass().getName();
            //获取注册中心
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            //获取注册中心的实例
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            //将服务名和版本号赋值给serviceMetaInfo
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            //得到服务名后，这里获取服务元信息
            List<ServiceMetaInfo> discoveryList = registry.discovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(discoveryList)) {
                throw new RuntimeException("暂无服务地址");
            }
            //通过负载均衡获取服务地址
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, discoveryList);
            // 发送 TCP 请求,使用重试策略
            RpcResponse rpcResponse;
            try {
                RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
                rpcResponse = retryStrategy.doRetry(() ->
                        VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
                );
            } catch (Exception e) {
                // 容错处理
                TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
                rpcResponse = tolerantStrategy.doTolerant(null, e);
            }

            return rpcResponse.getData();
        } catch (Exception e) {
            throw new RuntimeException("调用失败");
        }
    }
}
