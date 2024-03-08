package com.pandaer.pan.server.modules.share.enums;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ShareTypeEnum {

    NEED_SHARE_CODE(0, "需要提取码"),
    ;
    private final Integer code;

    private final String desc;
}
