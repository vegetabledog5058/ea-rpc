package com.siyi.earpc.protocol;

import lombok.Getter;

/**
 * @author Eric
 * 协议消息类型枚举
 */
@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);
    private final byte key;

    ProtocolMessageTypeEnum(int key) {
        this.key = (byte) key;
    }
    // 通过key获取对应的枚举
    public static ProtocolMessageTypeEnum getEnumByKey(byte key) {
        for(ProtocolMessageTypeEnum typeEnum : ProtocolMessageTypeEnum.values()) {
            if(typeEnum.key == key) {
                return typeEnum;
            }
        }
        return null;
    }
}
