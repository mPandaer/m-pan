package com.pandaer.pan.server.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@ApiModel("校验密码参数实体")
@Data
public class CheckAnswerPO {

    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$",message = "请输入6-16位且只包含字母，数字的用户名")
    private String username;

    @ApiModelProperty("密保答案")
    @NotBlank(message = "密保答案不能为空")
    @Length(max = 200,message = "密保答案的长度不能超过200个字符")
    private String answer;

}
