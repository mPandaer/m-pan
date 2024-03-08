package com.pandaer.pan.server.modules.share.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ShareStatusEnum {


    NORMAL(0, "正常"),
    FILE_DELETED(1, "有文件被删除"),
    ;
    private final Integer code;

    private final String desc;
}
