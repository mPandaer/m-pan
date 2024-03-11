package com.pandaer.pan.server.modules.share.po;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("保存到我的网盘参数实体")
public class SaveShareFilePO implements Serializable {

    @ApiModelProperty("要保存的文件ID")
    @NotEmpty(message = "要保存的文件列表不能为空")
    private List<String> fileIdList;

    @ApiModelProperty("保存到的目标目录")
    @NotBlank(message = "目标文件夹不能为空")
    private String targetParentId;
}
