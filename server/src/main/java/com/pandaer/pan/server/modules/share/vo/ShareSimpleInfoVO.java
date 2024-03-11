package com.pandaer.pan.server.modules.share.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("获取分享简略信息的响应实体")
@Data
public class ShareSimpleInfoVO implements Serializable {
    @ApiModelProperty("分享的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享的名字")
    private String shareName;

    @ApiModelProperty("分享的用户信息")
    private ShareUserInfoVO shareUserInfoVO;
}
