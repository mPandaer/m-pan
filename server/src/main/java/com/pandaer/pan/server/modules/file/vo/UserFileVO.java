package com.pandaer.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel("查询文件列表的返回实体")
@Data
public class UserFileVO implements Serializable {

    @ApiModelProperty("文件Id")
    @JsonSerialize(using = IdEncryptSerializer.class) //由于Long在JS中存在精度缺失问题，所以这里利用String作为特别的序列化
    private Long fileId;

    @ApiModelProperty("父文件夹Id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty("文件名")
    private String filename;

    @ApiModelProperty("文件夹标识")
    private Integer folderFlag;

    @ApiModelProperty("文件大小描述")
    private String fileSizeDesc;

    @ApiModelProperty("文件类型标识 （1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）")
    private Integer fileType;

    @ApiModelProperty("文件更新时间")
    private Date updateTime;

}
