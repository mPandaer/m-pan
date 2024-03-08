package com.pandaer.pan.server.modules.share.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("分享链接响应实体")
public class MPanShareUrlVO implements Serializable {

    /**
     * 分享ID
     */
    @ApiModelProperty("分享ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    /**
     * 分享名称
     */
    @ApiModelProperty("分享名称")
    private String shareName;

    /**
     * 分享的URL
     */
    @ApiModelProperty("分享的URL")
    private String shareUrl;

    /**
     * 分享的提取码
     */
    @ApiModelProperty("分享的提取码")
    private String shareCode;

    /**
     * 分享的类型 0: 正常  1: 由文件删除
     */
    @ApiModelProperty("分享的类型 0: 正常  1: 由文件删除")
    private Integer shareStatus;

}
