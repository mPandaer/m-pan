package com.pandaer.pan.server.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@ApiModel("校验用户名参数实体")
@Data
public class CheckUsernamePO implements Serializable {

    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$",message = "请输入6-16位且只包含字母，数字的用户名")
    private String username;
}
