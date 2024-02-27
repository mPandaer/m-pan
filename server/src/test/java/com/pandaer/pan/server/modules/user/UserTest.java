package com.pandaer.pan.server.modules.user;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.JwtUtil;
import com.pandaer.pan.server.modules.file.constants.FileConstants;
import com.pandaer.pan.server.modules.user.constants.UserConstants;
import com.pandaer.pan.server.modules.user.context.*;
import com.pandaer.pan.server.modules.user.service.IUserService;
import com.pandaer.pan.server.modules.user.vo.CurrentUserVO;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UserTest {

    @Autowired
    public IUserService userService;

    public static final String USERNAME = "bobo";
    public static final String PASSWORD = "12345678";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";

    // 注册成功
    @Test
    public void testRegisterSuccess() {
        Long register = userRegister();
        Assert.assertNotNull(register);
        Assert.assertTrue(register > 0);
    }

    //注册失败 -- 重复文件名
    @Test(expected = MPanBusinessException.class)
    public void testRegisterFailWithRepeatName() {
        Long register = userRegister();
        Assert.assertNotNull(register);
        Assert.assertTrue(register > 0);
        userRegister(); //重复注册
    }


    //登录成功
    @Test
    public void testLoginSuccess() {
        Long userId = userRegister();
        Assert.assertTrue(userId != null && userId > 0);
        String token = userLogin(USERNAME, PASSWORD);
        Assert.assertNotNull(token);
    }


    //登录失败 -- 密码不正确
    @Test(expected = MPanBusinessException.class)
    public void testLoginFailWithPassWordError() {
        Long userId = userRegister();
        Assert.assertTrue(userId != null && userId > 0);
        userLogin(USERNAME, PASSWORD + "x");
    }

    //登录失败 -- 用户不存在
    @Test(expected = MPanBusinessException.class)
    public void testLoginFailWithNameNotExist() {
        Long userId = userRegister();
        Assert.assertTrue(userId != null && userId > 0);
        userLogin(USERNAME + "x", PASSWORD);
    }

    //退出登录成功
    @Test
    public void testExitSuccess() {
        Long userId = userRegister();
        String token = userLogin(USERNAME,PASSWORD);
        userId = (Long) JwtUtil.analyzeToken(token, UserConstants.LOGIN_USER_ID_KEY);
        userService.exit(userId);
    }


    //校验用户名成功
    @Test
    public void testCheckUsernameSuccess() {
        userRegister();
        CheckUsernameContext context = new CheckUsernameContext();
        context.setUsername(USERNAME);
        String question = userService.checkUsername(context);
        Assert.assertEquals("question",question);
    }

    //校验用户名失败
    @Test(expected = MPanBusinessException.class)
    public void testCheckUsernameFailWithUserNotExist() {
        userRegister();
        CheckUsernameContext context = new CheckUsernameContext();
        context.setUsername(USERNAME + "x");
        String question = userService.checkUsername(context);
        Assert.assertEquals(question,"question");
    }


    //校验密保答案成功
    @Test
    public void testCheckAnswerSuccess() {
        Long userId = userRegister();
        CheckAnswerContext context = new CheckAnswerContext();
        context.setUsername(USERNAME);
        context.setAnswer(ANSWER);
        String token = userService.checkAnswer(context);
        Assert.assertTrue(StringUtils.isNotBlank(token));

    }

    //校验密保答案失败
    @Test(expected = MPanBusinessException.class)
    public void testCheckAnswerFailWithAnswerError() {
        Long userId = userRegister();
        CheckAnswerContext context = new CheckAnswerContext();
        context.setUsername(USERNAME);
        context.setAnswer(ANSWER + "x");
        String token = userService.checkAnswer(context);
        Assert.assertTrue(StringUtils.isNotBlank(token));
    }

    //重置密码成功
    @Test
    public void testResetPasswordSuccess() {
        userRegister();
        CheckAnswerContext context = new CheckAnswerContext();
        context.setUsername(USERNAME);
        context.setAnswer(ANSWER);
        String token = userService.checkAnswer(context);
        Assert.assertTrue(StringUtils.isNotBlank(token));
        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();
        resetPasswordContext.setToken(token);
        resetPasswordContext.setPassword("12345678900");
        resetPasswordContext.setUsername(USERNAME);
        userService.resetPassword(resetPasswordContext);
    }

    //重置密码失败 -- 校验Token不正确
    @Test(expected = MPanBusinessException.class)
    public void testResetPasswordFailWithTokenError() {
        Long userId = userRegister();
        CheckAnswerContext context = new CheckAnswerContext();
        context.setUsername(USERNAME);
        context.setAnswer(ANSWER);
        String token = userService.checkAnswer(context);
        Assert.assertTrue(StringUtils.isNotBlank(token));
        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();
        resetPasswordContext.setToken(token + "x");
        resetPasswordContext.setPassword("12345678900");
        userService.resetPassword(resetPasswordContext);
    }


    //更新密码成功
    @Test
    public void testChangePasswordSuccess() {
        Long userId = userRegister();
//        userLogin(USERNAME,PASSWORD);
        ChangePasswordContext context = new ChangePasswordContext();
        context.setNewPassword(PASSWORD + "new");
        context.setOldPassword(PASSWORD);
        context.setUserId(userId);
        userService.changePassword(context);
    }

    //更新密码失败
    @Test(expected = MPanBusinessException.class)
    public void testChangePasswordFailWithOldPasswordError() {
        Long userId = userRegister();
//        userLogin(USERNAME,PASSWORD);
        ChangePasswordContext context = new ChangePasswordContext();
        context.setNewPassword(PASSWORD + "new");
        context.setOldPassword(PASSWORD + "x");
        context.setUserId(userId);
        userService.changePassword(context);
    }

    //获取当前用户登录信息成功
    @Test
    public void testGetCurrentUser() {
        Long userId = userRegister();
        CurrentUserVO currentUser = userService.getCurrentUser(userId);
        Assert.assertNotNull(currentUser);
        Assert.assertEquals(USERNAME,currentUser.getUsername());
        Assert.assertEquals(FileConstants.ROOT_FOLDER_NAME_CN,currentUser.getRootFileName());
    }




    private String userLogin(String username, String password) {
        UserLoginContext context = new UserLoginContext();
        context.setUsername(username);
        context.setPassword(password);
        return userService.login(context);
    }

    private Long userRegister() {
        UserRegisterContext context = getUserRegisterContext();
        return userService.register(context);
    }

    private UserRegisterContext getUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }

}
