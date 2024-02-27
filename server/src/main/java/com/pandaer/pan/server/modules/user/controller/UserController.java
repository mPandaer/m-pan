package com.pandaer.pan.server.modules.user.controller;

import com.pandaer.pan.core.response.Resp;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.user.context.UserLoginContext;
import com.pandaer.pan.server.modules.user.context.UserRegisterContext;
import com.pandaer.pan.server.modules.user.convertor.UserConverter;
import com.pandaer.pan.server.modules.user.po.UserLoginPO;
import com.pandaer.pan.server.modules.user.po.UserRegisterPO;
import com.pandaer.pan.server.modules.user.service.IUserService;
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
    public Resp<Long> register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        UserRegisterContext context = userConverter.PO2ContextInRegister(userRegisterPO);
        Long userId = userService.register(context);
        return Resp.successAndData(userId);
    }


    @ApiOperation(value = "用户登陆接口",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PostMapping("login")
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
}
