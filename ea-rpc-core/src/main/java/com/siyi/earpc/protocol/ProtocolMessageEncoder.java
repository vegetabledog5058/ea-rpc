package com.siyi.earpc.protocol;

import com.siyi.earpc.serializer.Serializer;
import com.siyi.earpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;
/**
 * 协议消息编码器
 * @author Eric
 */
public class ProtocolMessageEncoder {
    /**
     * 编码
     * @param protocolMessage 协议消息
     * @return 编码后的字节数组
     * @throws IOException 序列化异常
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        //获取序列化器的枚举
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            return Buffer.buffer();
        }
        //获取序列化器实例
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        //序列化消息体
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // 写入 body 长度和数据
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;

    }
}
