package com.siyi.earpc.server.tcp;

import com.siyi.earpc.RpcApplication;
import com.siyi.earpc.model.RpcRequest;
import com.siyi.earpc.model.RpcResponse;
import com.siyi.earpc.protocol.ProtocolMessage;
import com.siyi.earpc.protocol.ProtocolMessageDecoder;
import com.siyi.earpc.protocol.ProtocolMessageEncoder;
import com.siyi.earpc.protocol.ProtocolMessageStatusEnum;
import com.siyi.earpc.register.LocalRegistry;
import com.siyi.earpc.serializer.Serializer;
import com.siyi.earpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Eric
 * TCP服务端处理器
 */
public class TcpServerHandler implements Handler<NetSocket> {
    /**
     * 处理请求
     *
     * @param netSocket socket
     */
    @Override
    public void handle(NetSocket netSocket) {
        // 1. 转换为对象,从对象中获得参数
        //指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        //记录日志
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 处理请求代码

            netSocket.handler(b -> {
                ProtocolMessage<RpcRequest> protocolMessage;
                try {
                    protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(b);
                } catch (IOException e) {
                    throw new RuntimeException("协议消息解码错误");
                }
                RpcRequest rpcRequest = protocolMessage.getBody();
                //处理请求
                //构建响应对象
                RpcResponse rpcResponse = new RpcResponse();

                //从注册器获取实现类,反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getName());
                try {
                    Method methods = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                    Object result = methods.invoke(implClass.newInstance(), rpcRequest.getArgs());
                    //返回结果
                    rpcResponse.setData(result);
                    rpcResponse.setDataType(methods.getReturnType());
                    rpcResponse.setMessage("ok");
                } catch (Exception e) {
                    e.printStackTrace();
                    rpcResponse.setException(e);
                    rpcResponse.setMessage(e.getMessage());

                }
                //返回响应
                ProtocolMessage.Header header = new ProtocolMessage.Header();
                header.setType((byte) ProtocolMessageStatusEnum.OK.getValue());
                ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
                try {
                    Buffer encode = ProtocolMessageEncoder.encode(rpcResponseProtocolMessage);
                    netSocket.write(encode);
                } catch (IOException e) {
                    throw new RuntimeException("协议消息编码错误");
                }

            });

        });
        netSocket.handler(bufferHandlerWrapper);
    }
}
