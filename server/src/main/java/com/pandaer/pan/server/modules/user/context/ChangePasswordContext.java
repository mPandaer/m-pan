package com.pandaer.pan.server.modules.user.context;

import com.pandaer.pan.server.modules.user.domain.MPanUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data

public class ChangePasswordContext {

    private String oldPassword;

    private String newPassword;

    private Long userId;

    private MPanUser entity;
}
