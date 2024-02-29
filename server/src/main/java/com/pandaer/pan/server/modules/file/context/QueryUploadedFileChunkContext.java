package com.pandaer.pan.server.modules.file.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class QueryUploadedFileChunkContext {

    private String identifier;

    private Long userId;
}
