package com.pandaer.pan.server.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("查询已经上传的文件分片列表返回实体")
@Data
public class UploadedFileChunkVO implements Serializable {

    @ApiModelProperty("已经上传的文件分片列表")
    private List<Integer> uploadedChunkNumberList;
}
