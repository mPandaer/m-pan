package com.pandaer.pan.server.modules.user.service.cache.keygenerator;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

@Component("userIdKeyGenerator")
public class UserIdKeyGenerator implements KeyGenerator {

    private static final String USER_ID_PREFIX = "USER:ID:";

    @Override
    public Object generate(Object target, Method method, Object... args) {
        StringBuilder sb = new StringBuilder(USER_ID_PREFIX);
        if (args.length == 0) {
            return sb.toString();
        }
        Serializable id;

        for (Object arg : args) {
            if (arg instanceof Serializable) {
                id = (Serializable) arg;
                sb.append(id);
                return sb.toString();
            }
        }
        sb.append(StringUtils.arrayToCommaDelimitedString(args));
        return sb.toString();
    }
}
