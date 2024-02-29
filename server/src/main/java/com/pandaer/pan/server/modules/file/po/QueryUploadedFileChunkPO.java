package com.pandaer.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("获取文件已经上传的文件分片列表")
@Data
public class QueryUploadedFileChunkPO {

    @NotBlank(message = "文件唯一标识不能为空")
    @ApiModelProperty("文件唯一标识")
    private String identifier;
}
