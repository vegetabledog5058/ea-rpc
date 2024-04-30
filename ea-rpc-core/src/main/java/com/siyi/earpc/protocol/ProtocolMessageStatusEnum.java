package com.siyi.earpc.protocol;

import lombok.Getter;

/**
 * @author Eric
 * 协议消息状态枚举
 */

@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);

    private final String text;
    private final int value;

     ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }
    // 通过value获取对应的枚举
    public static ProtocolMessageStatusEnum fromValue(int value) {
         for(ProtocolMessageStatusEnum statusEnum : ProtocolMessageStatusEnum.values()) {
             if(statusEnum.value == value) {
                 return statusEnum;
             }
         }
         return null;
    }

}
