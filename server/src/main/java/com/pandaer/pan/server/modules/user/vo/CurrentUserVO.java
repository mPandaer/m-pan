package com.pandaer.pan.server.modules.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("登录用户信息响应实体")
@Data
public class CurrentUserVO implements Serializable {


    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("加密的根目录Id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long rootFileId;

    @ApiModelProperty("根目录名")
    private String rootFileName;

}
