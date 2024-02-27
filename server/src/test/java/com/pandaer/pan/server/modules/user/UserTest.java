package com.pandaer.pan.server.modules.user;

import com.pandaer.pan.core.exception.MPanBusinessException;
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

    // 注册成功
    @Test
    public void testRegisterSuccess() {
        UserRegisterContext context = getUserRegisterContext();
        Long register = userService.register(context);
        Assert.assertNotNull(register);
        Assert.assertTrue(register > 0);
    }

    //注册失败 -- 重复文件名
    @Test(expected = MPanBusinessException.class)
    public void testRegisterFailWithRepeatName() {
        UserRegisterContext context = getUserRegisterContext();
        Long register = userService.register(context);
        Assert.assertNotNull(register);
        Assert.assertTrue(register > 0);
        userService.register(context); //重复注册
    }

    private UserRegisterContext getUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername("pandaer");
        context.setPassword("12345678");
        context.setQuestion("question");
        context.setQuestion("answer");
        return context;
    }

}
