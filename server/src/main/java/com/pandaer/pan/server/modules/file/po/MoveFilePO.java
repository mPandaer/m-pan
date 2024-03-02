package com.pandaer.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@ApiModel("移动文件参数实体")
@Data
public class MoveFilePO {

    @ApiModelProperty("移动的文件Id列表")
    @NotEmpty(message = "文件Id不能为空")
    private List<String> fileIdList;

    @ApiModelProperty("目标文件夹Id")
    @NotBlank(message = "目标文件夹Id不能为空")
    private String targetParentId;
}
