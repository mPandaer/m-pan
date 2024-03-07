package com.pandaer.pan.server.modules.recycle.po;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel("还原文件参数实体")
public class RestoreFilePO {


    @ApiModelProperty(value = "需要还原的文件id列表",required = true)
    @NotEmpty(message = "文件id列表不能为空")
    private List<String> fileIdList;
}
