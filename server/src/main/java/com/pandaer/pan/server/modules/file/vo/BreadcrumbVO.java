package com.pandaer.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("面包屑VO")
@Data
public class BreadcrumbVO implements Serializable {

    @ApiModelProperty("文件id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long fileId;
    @ApiModelProperty("面包屑名称")
    private String filename;
}
