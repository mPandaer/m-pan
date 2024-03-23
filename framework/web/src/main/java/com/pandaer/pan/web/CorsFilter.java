package com.pandaer.pan.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "corsFilter")
@Order(1)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 获取请求的来源（即域名）
        String origin = request.getHeader("Origin");
        if (StringUtils.isEmpty(origin)) {
            origin = request.getHeader("Referer");
        }
        if (StringUtils.isEmpty(origin)) {
            origin = request.getHeader("Host");
        }
        if (StringUtils.isEmpty(origin)) {
            origin = "*";
        }
        // 允许该来源的跨域请求
        response.setHeader("Access-Control-Allow-Origin", origin);
        
        // 允许跨域请求的域名，* 表示允许所有域名
//        response.setHeader("Access-Control-Allow-Origin", "*");
        // 允许的请求方法
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // 允许的请求头字段
        response.setHeader("Access-Control-Allow-Headers", "*");
        // 是否允许浏览器在跨域请求中携带身份凭证（例如：Cookies）
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 预检请求的有效期，单位：秒
        response.setHeader("Access-Control-Max-Age", "3600");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            // 对于预检请求（OPTIONS），直接返回 200 OK，以允许实际请求继续执行
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // 对于其他请求，继续执行过滤器链
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
