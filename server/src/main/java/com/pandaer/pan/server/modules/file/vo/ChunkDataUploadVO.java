package com.pandaer.pan.server.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("文件分片上传返回实体")
@Data
public class ChunkDataUploadVO implements Serializable {
    @ApiModelProperty("这次请求上传的分片序号")
    private Integer chunkNumber;

    @ApiModelProperty("是否需要合并操作")
    private Integer merge;

}
