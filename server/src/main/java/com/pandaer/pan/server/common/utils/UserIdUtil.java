package com.pandaer.pan.server.common.utils;

import com.pandaer.pan.core.exception.MPanFrameworkException;

public class UserIdUtil {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        Long userId = USER_ID.get();
        if (userId == null) {
            throw new MPanFrameworkException("获取当前登录信息失败");
        }
        return userId;
    }
}
