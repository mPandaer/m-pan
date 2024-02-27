package com.pandaer.pan.server.modules.user.controller;

import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.server.common.annotation.LoginIgnore;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.user.context.*;
import com.pandaer.pan.server.modules.user.convertor.UserConverter;
import com.pandaer.pan.server.modules.user.po.*;
import com.pandaer.pan.server.modules.user.service.IUserService;
import com.pandaer.pan.server.modules.user.vo.CurrentUserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(value = "用户模块")
@RequestMapping("/user")
@RestController
public class UserController {


    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "用户注册接口",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("register")
    @LoginIgnore
    public Resp<Long> register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        UserRegisterContext context = userConverter.PO2ContextInRegister(userRegisterPO);
        Long userId = userService.register(context);
        return Resp.successAndData(userId);
    }


    @ApiOperation(value = "用户登陆接口",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("login")
    @LoginIgnore
    public Resp<String> login(@Validated @RequestBody UserLoginPO userLoginPO) {
        UserLoginContext context = userConverter.PO2ContextInLogin(userLoginPO);
        String accessToken = userService.login(context);
        return Resp.successAndData(accessToken);
    }

    @ApiOperation(value = "用户退出接口",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @GetMapping("exit")
    public Resp<Object> exit() {
        userService.exit(UserIdUtil.getUserId());
        return Resp.success();
    }


    @ApiOperation(value = "忘记密码--校验用户名",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("username/check")
    @LoginIgnore
    public Resp<String> checkUsername(@Validated @RequestBody CheckUsernamePO checkUsernamePO) {
        CheckUsernameContext context = userConverter.PO2ContextInCheckUsername(checkUsernamePO);
        String question = userService.checkUsername(context);
        return Resp.successAndData(question);
    }


    @ApiOperation(value = "忘记密码--校验密保答案",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("answer/check")
    @LoginIgnore
    public Resp<String> checkAnswer(@Validated @RequestBody CheckAnswerPO checkAnswerPO) {
        CheckAnswerContext context = userConverter.PO2ContextInCheckAnswer(checkAnswerPO);
        String token = userService.checkAnswer(context);
        return Resp.successAndData(token);
    }

    @ApiOperation(value = "忘记密码--修改密码",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("password/reset")
    @LoginIgnore
    public Resp<Object> resetPassword(@Validated @RequestBody ResetPasswordPO resetPasswordPO) {
        ResetPasswordContext context = userConverter.PO2ContextInResetPassword(resetPasswordPO);
        userService.resetPassword(context);
        return Resp.success();
    }

    @ApiOperation(value = "修改密码",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("password/change")
    public Resp<Object> changePassword(@Validated @RequestBody ChangePasswordPO changePasswordPO) {
        ChangePasswordContext context = userConverter.PO2ContextInChangePassword(changePasswordPO);
        context.setUserId(UserIdUtil.getUserId());
        userService.changePassword(context);
        return Resp.success();
    }

    @ApiOperation(value = "获取当前登录用户信息",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @GetMapping("current")
    public Resp<CurrentUserVO> current() {
        CurrentUserVO currentUserVO = userService.getCurrentUser(UserIdUtil.getUserId());
        return Resp.successAndData(currentUserVO);
    }
}
