package com.pandaer.pan.server.modules.file.po;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@ApiModel("创建文件夹的参数实体")
@Data
public class CreateFolderPO implements Serializable {

    @ApiModelProperty("加密的父文件夹ID")
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @ApiModelProperty("文件夹名")
    @NotBlank(message = "文件夹名不能为空")
    private String folderName;

}
