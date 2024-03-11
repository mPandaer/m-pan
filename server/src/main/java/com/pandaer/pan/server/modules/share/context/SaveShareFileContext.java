package com.pandaer.pan.server.modules.share.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
public class SaveShareFileContext implements Serializable {

    /**
     * 保存的文件ID列表
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹ID
     */
    private Long targetParentId;

    /**
     * 当前登录用户
     */
    private Long userId;

    /**
     * 这条分享的ID
     */
    private Long shareId;
}
