package com.pandaer.pan.server.modules.user.service;

import com.pandaer.pan.server.modules.user.context.*;
import com.pandaer.pan.server.modules.user.domain.MPanUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.pan.server.modules.user.vo.CurrentUserVO;

/**
* @author pandaer
* @description 针对表【m_pan_user(用户信息表)】的数据库操作Service
* @createDate 2024-02-25 18:35:18
*/
public interface IUserService extends IService<MPanUser> {

    Long register(UserRegisterContext context);

    String login(UserLoginContext context);

    void exit(Long userId);

    String checkUsername(CheckUsernameContext context);

    String checkAnswer(CheckAnswerContext context);

    void resetPassword(ResetPasswordContext context);

    void changePassword(ChangePasswordContext context);

    CurrentUserVO getCurrentUser(Long userId);
}
