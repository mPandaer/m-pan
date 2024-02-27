package com.pandaer.pan.server.common.aspect;

import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.JwtUtil;
import com.pandaer.pan.server.common.annotation.LoginIgnore;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.user.constants.UserConstants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Component
@Aspect
@Log4j2
public class LoginAspect {

    public static final String LOGIN_AUTH_PARAM_NAME = "authorization";
    public static final String LOGIN_AUTH_REQUEST_HEADER_NAME = "Authorization";
    public static final String POINT_CUT = "execution(* com.pandaer.pan.server.modules.*.controller..*(..))";

    @Autowired
    private Cache panCache;

    @Pointcut(value = POINT_CUT)
    public void authPoint(){}

    @Before("authPoint()")
    public void loginAuth(JoinPoint joinPoint) {
        if(!checkAuth(joinPoint)) {
            return;
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        log.info("拦截的URL: " + request.getRequestURI());
        String requestToken = getRequestToken(request);
        if (StringUtils.isBlank(requestToken)) {
            throw new MPanBusinessException("没有登录凭证");
        }
        Long userId = (Long) JwtUtil.analyzeToken(requestToken, UserConstants.LOGIN_USER_ID_KEY);
        if (userId == null) {
            throw new MPanBusinessException("登录凭证不合法");
        }
        String cacheToken = panCache.get(UserConstants.CACHE_LOGIN_USER_ID_PREFIX + userId, String.class);
        if (cacheToken == null) {
            throw new MPanBusinessException("用户未登录");
        }
        if (!StringUtils.equals(requestToken,cacheToken)) {
            throw new MPanBusinessException("登录凭证失效");
        }
        UserIdUtil.setUserId(userId);
    }

    private String getRequestToken(HttpServletRequest request) {
        String token = request.getHeader(LOGIN_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(LOGIN_AUTH_PARAM_NAME);
        }
        return token;
    }

    private boolean checkAuth(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return !method.isAnnotationPresent(LoginIgnore.class);
    }
}
