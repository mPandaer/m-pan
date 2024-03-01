package com.pandaer.pan.server.modules.file.po;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("合并文件参数实体")
@Data
public class MergeChunkFilePO {

    @ApiModelProperty("加密的父文件夹ID")
    @NotBlank(message = "文件夹ID不能为空")
    private String parentId;

    @ApiModelProperty("文件名")
    @NotBlank(message = "文件夹ID不能为空")
    private String filename;

    @ApiModelProperty("文件唯一标识")
    @NotBlank(message = "文件夹ID不能为空")
    private String identifier;

    @ApiModelProperty("文件总大小")
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @ApiModelProperty("文件总分片数")
    @NotNull(message = "该文件分片总数不能为空")
    private Long totalChunks;

}
