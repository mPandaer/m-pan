package com.pandaer.pan.server.modules.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("用户历史返回实体")
@AllArgsConstructor
public class UserHistoryVO implements Serializable {

    @ApiModelProperty("搜素文案")
    private String value;

}
