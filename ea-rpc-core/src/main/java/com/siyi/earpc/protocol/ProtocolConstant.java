package com.siyi.earpc.protocol;

/**
 * @author Eric
 * 协议常量
 */
public interface ProtocolConstant {
    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;
    /**
     * 魔数
     */
    byte MAGIC = 0x1;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}
