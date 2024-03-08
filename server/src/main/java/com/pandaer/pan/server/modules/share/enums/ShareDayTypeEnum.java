package com.pandaer.pan.server.modules.share.enums;


import com.pandaer.pan.core.exception.MPanBusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
public enum ShareDayTypeEnum {

    PERMANENT_VALIDITY(0, 0,"永久有效"),
    SEVEN_DAYS_VALIDITY(1, 7,"7天有效"),
    THIRTY_DAYS_VALIDITY(2, 30,"30天有效"),

    ;
    private final Integer code;

    private final Integer days;
    private final String desc;

    public static Integer getDaysByCode(Integer code){
        if (Objects.isNull(code)) {
            throw new MPanBusinessException("分享时间类型不能为空");
        }
        for (ShareDayTypeEnum value : ShareDayTypeEnum.values()) {
            if (value.getCode().equals(code)){
                return value.getDays();
            }
        }
        throw new MPanBusinessException("分享时间类型不合法");
    }
}
