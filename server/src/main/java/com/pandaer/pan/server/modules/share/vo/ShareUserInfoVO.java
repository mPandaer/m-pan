package com.pandaer.pan.server.modules.share.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("分享用户信息")
@Data
public class ShareUserInfoVO implements Serializable {

    @ApiModelProperty("用户id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;
}
