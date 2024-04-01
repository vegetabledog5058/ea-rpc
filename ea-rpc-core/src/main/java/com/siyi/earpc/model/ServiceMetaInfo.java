package com.siyi.earpc.model;

import cn.hutool.core.util.StrUtil;
import com.siyi.earpc.constant.RpcConstant;
import lombok.Data;

/**
 * @author Eric
 */
@Data
public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务版本号
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
    /**
     * 服务地址
     */
    private String serviceAddress;
    /**
     * 服务域名
     */
    private String serviceHost;
    /**
     * 服务端口
     */
    private int servicePort;
    /**
     * 服务分组
     */
    private String serviceGroup = "default";

    /**
     * 获取服务键名
     *
     * @return 服务键名
     */
    public String getServiceKey() {
        // 后续可扩展服务分组
//  return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务注册节点键名
     *
     * @return 服务注册节点键名
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s", getServiceKey(), serviceAddress);
    }

    /**
     * 获取完整服务地址
     *
     * @return 服务地址
     */
    public String getServiceAddr() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
