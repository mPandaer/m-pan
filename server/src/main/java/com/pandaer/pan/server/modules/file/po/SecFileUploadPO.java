package com.pandaer.pan.server.modules.file.po;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("文件秒传参数实体")
@Data
public class SecFileUploadPO {

    @ApiModelProperty("加密的父文件夹ID")
    @NotBlank(message = "加密的父文件夹ID不能为空")
    private String parentId;

    @ApiModelProperty("加密的父文件夹ID")
    @NotBlank(message = "上传的文件名不能为空")
    private String filename;
    @ApiModelProperty("加密的父文件夹ID")
    @NotBlank(message = "基于文件内容的唯一标识不能为空")
    private String identifier;
}
