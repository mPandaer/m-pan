package com.pandaer.pan.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel("取消分享请求实体")
public class CancelSharesPO {

    @ApiModelProperty(value = "分享ID列表",required = true)
    @NotEmpty(message = "分享ID列表不能为空")
    private List<String> shareIdList;
}
