package com.pandaer.pan.server.modules.user.constants;

/**
 * 用户模块常量
 */
public interface UserConstants {
    String LOGIN_USER_ID_KEY = "LOGIN_USER_ID";
    String FORGET_USER_ID_KEY = "FORGET_USER_ID";

    String CACHE_LOGIN_USER_ID_PREFIX = "LOGIN_USER_ID_";

    String CACHE_FORGET_USER_ID_PREFIX = "FORGET_USER_ID_";

    Long ONE_DAY_TIME_LONG = 24L * 60L * 60L * 1000L;

    Long FIVE_MIN_TIME_LONG = 5L * 60L * 1000L;
}
