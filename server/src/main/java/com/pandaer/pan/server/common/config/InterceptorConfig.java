package com.pandaer.pan.server.common.config;

import com.pandaer.pan.server.common.interceptor.BloomFilterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private List<BloomFilterInterceptor> bloomFilterInterceptors;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        for (BloomFilterInterceptor bloomFilterInterceptor : bloomFilterInterceptors) {
            registry.addInterceptor(bloomFilterInterceptor)
                    .addPathPatterns(bloomFilterInterceptor.patterns())
                    .excludePathPatterns(bloomFilterInterceptor.excludePatterns());
        }
    }
}
