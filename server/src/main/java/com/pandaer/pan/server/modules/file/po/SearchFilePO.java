package com.pandaer.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel("搜索文件的参数实体")
@Data
public class SearchFilePO {
    @ApiModelProperty(value = "搜索关键字", required = true)
    @NotBlank(message = "搜索关键字不能为空")
    private String keyword;
    @ApiModelProperty("文件类型列表 多种文件类型用逗号分隔")
    private String fileType;
}
