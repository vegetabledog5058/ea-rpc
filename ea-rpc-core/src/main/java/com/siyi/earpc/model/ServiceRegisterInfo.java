package com.siyi.earpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Eric
 * 服务注册信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRegisterInfo<T> {
    // 服务名称
    private String serviceName;
    // 服务实现类
    private Class<?extends T>implClass;
}
