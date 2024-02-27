package com.pandaer.pan.server.modules.user;

import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.server.modules.user.context.UserLoginContext;
import com.pandaer.pan.server.modules.user.context.UserRegisterContext;
import com.pandaer.pan.server.modules.user.service.IUserService;
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

    public static final String USERNAME = "pandaer";
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
        Assert.assertTrue( userId != null && userId > 0);
        String token = userLogin(USERNAME,PASSWORD);
        Assert.assertNotNull(token);
    }


    //登录失败 -- 密码不正确
    @Test(expected = MPanBusinessException.class)
    public void testLoginFailWithPassWordError() {
        Long userId = userRegister();
        Assert.assertTrue( userId != null && userId > 0);
        userLogin(USERNAME,PASSWORD + "x");
    }

    //登录失败 -- 用户不存在
    @Test(expected = MPanBusinessException.class)
    public void testLoginFailWithNameNotExist() {
        Long userId = userRegister();
        Assert.assertTrue( userId != null && userId > 0);
        userLogin(USERNAME + "x",PASSWORD);
        System.out.println("-----------");
    }





    private String userLogin(String username,String password) {
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
        context.setQuestion(ANSWER);
        return context;
    }

}
