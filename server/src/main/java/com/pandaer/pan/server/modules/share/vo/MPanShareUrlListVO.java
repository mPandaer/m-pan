package com.pandaer.pan.server.modules.share.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.Date2StringSerializer;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("分享链接列表响应实体")
public class MPanShareUrlListVO {

    @ApiModelProperty("分享ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享名称")
    private String shareName;

    @ApiModelProperty("分享的URL")
    private String shareUrl;

    @ApiModelProperty("分享的提取码")
    private String shareCode;

    @ApiModelProperty("分享的类型 0: 正常  1: 由文件删除")
    private Integer shareStatus;

    @ApiModelProperty("分享的时间类型 0: 永久有效  1: 7天有效  2: 30天有效")
    private Integer shareDayType;

    @ApiModelProperty("分享的类型 0: 有提取码")
    private Integer shareType;

    @ApiModelProperty("分享的结束时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date shareEndTime;

    @ApiModelProperty("分享的创建时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date createTime;


}
