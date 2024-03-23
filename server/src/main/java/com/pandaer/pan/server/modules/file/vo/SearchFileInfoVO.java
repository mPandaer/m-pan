package com.pandaer.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.Date2StringSerializer;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("搜索文件的返回实体")
public class SearchFileInfoVO implements Serializable {
    @ApiModelProperty("文件Id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long fileId;
    @ApiModelProperty("父文件夹Id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty("父文件夹名称")
    private String parentName;

    @ApiModelProperty("文件名")
    private String filename;
    @ApiModelProperty("文件夹标识")
    private Integer folderFlag;
    @ApiModelProperty("文件大小描述")
    private String fileSizeDesc;
    @ApiModelProperty("文件类型")
    private Integer fileType;
    @ApiModelProperty("文件更新事件")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date updateTime;
}
