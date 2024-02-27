package com.pandaer.pan.server.modules.user.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;


@Data
public class ResetPasswordContext {
    private String password;
    private String username;

    private String token;

    private Long userId;
}
