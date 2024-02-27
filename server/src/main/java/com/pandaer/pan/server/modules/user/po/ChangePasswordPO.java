package com.pandaer.pan.server.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("修改密码参数实体")
public class ChangePasswordPO {
    @ApiModelProperty("旧密码")
    @NotBlank(message = "旧不能为空")
    @Length(min = 6,max = 16,message = "请输入长度为6-16的旧密码")
    private String oldPassword;

    @ApiModelProperty("新密码")
    @NotBlank(message = "新不能为空")
    @Length(min = 6,max = 16,message = "请输入长度为6-16的新密码")
    private String newPassword;
}
