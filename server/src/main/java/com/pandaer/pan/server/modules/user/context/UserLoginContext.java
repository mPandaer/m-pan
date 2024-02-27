package com.pandaer.pan.server.modules.user.context;

import com.pandaer.pan.server.modules.user.domain.MPanUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户登录逻辑的上下文实体
 */
@Data
public class UserLoginContext implements Serializable {
    private String username;

    private String password;

    private String accessToken;

    private MPanUser entity;
}
