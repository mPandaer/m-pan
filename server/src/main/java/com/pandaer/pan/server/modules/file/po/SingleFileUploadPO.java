package com.pandaer.pan.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel("单文件上传文件参数实体")
@Data
public class SingleFileUploadPO{

    @ApiModelProperty("加密的父文件夹ID")
    @NotBlank(message = "父文件夹不能为空")
    private String parentId;

    @ApiModelProperty("上传的文件名")
    @NotBlank(message = "上传的文件名不能为空")
    private String filename;

    @ApiModelProperty("上传的文件标识")
    @NotBlank(message = "文件标识不能为空")
    private String identifier;

    @ApiModelProperty("文件总大小")
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @ApiModelProperty("上传的文件数据")
    @NotNull(message = "文件数据不能为空")
    private MultipartFile fileData;

}
