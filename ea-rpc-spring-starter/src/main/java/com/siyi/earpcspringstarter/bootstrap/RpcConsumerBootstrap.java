package com.siyi.earpcspringstarter.bootstrap;

import com.siyi.earpc.proxy.ServiceProxyFactory;
import com.siyi.earpcspringstarter.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @author Eric
 * RPC服务消费者
 */
public class RpcConsumerBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        //遍历对象所有字段
        Field[] fields = beanClass.getFields();
        for (Field field : fields) {
            //获取字段上的注解
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                //获取接口类
                Class<?> interfaceClass = rpcReference.interfaceClass();
                //默认值处理,如果是void.class,则获取接口
                if (interfaceClass == void.class) {
                    //获取接口
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxy = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxy);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("RPC服务消费者注入代理对象失败", e);
                }

            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
