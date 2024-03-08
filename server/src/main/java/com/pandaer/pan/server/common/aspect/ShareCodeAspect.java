package com.pandaer.pan.server.common.aspect;

import com.pandaer.pan.core.exception.MPanBusinessException;
import com.pandaer.pan.core.utils.JwtUtil;
import com.pandaer.pan.server.common.annotation.LoginIgnore;
import com.pandaer.pan.server.common.utils.ShareIdUtil;
import com.pandaer.pan.server.common.utils.UserIdUtil;
import com.pandaer.pan.server.modules.share.constants.ShareConstants;
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
public class ShareCodeAspect {

    public static final String SHARE_CODE_PARAM_NAME = "shareToken";
    public static final String SHARE_CODE_REQUEST_HEADER_NAME = "Share-Token";
    public static final String POINT_CUT = "@annotation(com.pandaer.pan.server.common.annotation.NeedShareCode)";


    @Pointcut(value = POINT_CUT)
    public void shareCodeAuth() {
    }


    @Before("shareCodeAuth()")
    public void validShareCode(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        log.info("拦截的URL: " + request.getRequestURI());
        String requestToken = getRequestToken(request);
        if (StringUtils.isBlank(requestToken)) {
            throw new MPanBusinessException("没有提取码信息");
        }
        Long shareId = (Long) JwtUtil.analyzeToken(requestToken, ShareConstants.SHARE_ID);
        if (shareId == null) {
            throw new MPanBusinessException("提取码过期");
        }
        ShareIdUtil.setShareId(shareId);
    }

    private String getRequestToken(HttpServletRequest request) {
        String token = request.getHeader(SHARE_CODE_REQUEST_HEADER_NAME);
        return token == null ? request.getParameter(SHARE_CODE_PARAM_NAME) : token;
    }

}
