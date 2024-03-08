package com.pandaer.pan.server.modules.share.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pandaer.pan.serializer.Date2StringSerializer;
import com.pandaer.pan.serializer.IdEncryptSerializer;
import com.pandaer.pan.server.modules.file.vo.UserFileVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("分享详情响应实体")
public class ShareDetailVO implements Serializable {

    @ApiModelProperty("分享id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享名称")
    private String shareName;

    @ApiModelProperty("创建的时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date createTime;

    @ApiModelProperty("分享的类型")
    private Integer shareDayType;

    @ApiModelProperty("分享的截至时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date shareEndTime;

    @ApiModelProperty("分享的文件列表")
    private List<UserFileVO> userFileList;

    @ApiModelProperty("分享的用户信息")
    private ShareUserInfoVO userInfo;

}
