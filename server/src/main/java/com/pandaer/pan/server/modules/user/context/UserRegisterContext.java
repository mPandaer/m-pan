package com.pandaer.pan.server.modules.user.context;

import com.pandaer.pan.server.modules.user.domain.MPanUser;
import lombok.Data;

/**
 * 用户注册逻辑链的上下文对象
 */
@Data
public class UserRegisterContext {
    private String username;

    private String password;

    private String question;

    private String answer;

    private MPanUser entity;
}
