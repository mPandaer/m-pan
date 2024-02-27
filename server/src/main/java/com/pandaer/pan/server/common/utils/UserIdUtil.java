package com.pandaer.pan.server.common.utils;



public class UserIdUtil {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        Long userId = USER_ID.get();
        if (userId == null) {
           return -1L;
        }
        return userId;
    }
}
