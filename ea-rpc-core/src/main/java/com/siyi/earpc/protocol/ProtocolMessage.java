package com.siyi.earpc.protocol;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Eric
 * 协议消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private T body;
    @Data
    public static class Header {
        /**
         * 魔数
         */
        private byte magic;
        /**
         * 协议版本
         */
        private byte version;
        /**
         * 序列化器
         */
        private byte serializer;
        /**
         * 状态
         */
        private byte status;
        /**
         * 消息类型
         */
        private byte type;
        /**
         * 消息 ID
         */
        private long requestId;
        /**
         * 数据长度
         */
        private int bodyLength;

    }


}
