package com.siyi.earpc.model;

import com.siyi.earpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Eric
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    //服务名称
    private String name;
    //方法名称
    private String methodName;
    //调用参数的类型列表
    private Class<?>[] parameterTypes;
    //    参数列表
    private Object[] args;
    //    服务版本号
    private String serviceVersion  = RpcConstant.DEFAULT_SERVICE_VERSION;
}
