package com.siyi.earpc.protocol;

import com.siyi.earpc.model.RpcRequest;
import com.siyi.earpc.model.RpcResponse;
import com.siyi.earpc.serializer.Serializer;
import com.siyi.earpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息解码器
 * @auther Eric
 */
public class ProtocolMessageDecoder {
    /**
     * 解码
     * @param buffer 缓冲区
     * @return 协议消息
     * @throws IOException 反序列化异常
     */
    public static ProtocolMessage decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        //读取buffer
        byte magic = buffer.getByte(0);
        //校验魔数
        if (magic != ProtocolConstant.MAGIC) {
            throw new RuntimeException("消息magic不合法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        int bodyLength = buffer.getInt(13);
        //解决粘包问题
        byte[] bodyBytes = buffer.getBytes(17, 17 + bodyLength);
        header.setBodyLength(bodyLength);
        //获取序列化器的枚举
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化器不合法");
        }
        //获取序列化器实例
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum protocolMessageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (protocolMessageTypeEnum == null) {
            throw new RuntimeException("消息类型不合法");
        }
        switch (protocolMessageTypeEnum) {
            case REQUEST:
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, rpcRequest);
            case RESPONSE:
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, rpcResponse);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持的消息类型");
        }

    }
}
