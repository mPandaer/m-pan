package com.pandaer.pan.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("创建分享链接请求实体")
public class CreateShareUrlPO {

    /**
     * 分享名称
     */
    @ApiModelProperty(value = "分享名称",required = true)
    @NotBlank(message = "分享名称不能为空")
    private String shareName;

    /**
     * 分享类型（0 有提取码）
     */
    @ApiModelProperty("分享类型（0 有提取码）")
    @NotNull(message = "分享类型不能为空")
    private Integer shareType;

    /**
     * 分享时间类型（0 永久有效；1 7天有效；2 30天有效）
     */
    @ApiModelProperty("分享时间类型（0 永久有效；1 7天有效；2 30天有效）")
    @NotNull(message = "分享时间类型不能为空")
    private Integer shareDayType;


    /**
     * 分享的文件加密ID列表
     */
    @ApiModelProperty("分享的文件加密ID列表")
    @NotEmpty(message = "文件列表不能为空")
    private List<String> shareFileIdList;
}
