package com.pandaer.pan.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("检查分享提取码请求实体")
public class CheckShareCodePO {

    @ApiModelProperty("加密的分享ID")
    @NotBlank(message = "分享ID不能为空")
    private String shareId;

    @ApiModelProperty("提取码")
    @NotBlank(message = "提取码不能为空")
    @Length(min = 4, max = 4, message = "提取码长度为4")
    private String shareCode;
}
