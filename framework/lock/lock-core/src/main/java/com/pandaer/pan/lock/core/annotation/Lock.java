package com.pandaer.pan.lock.core.annotation;

import com.pandaer.pan.lock.core.key.KeyGenerator;
import com.pandaer.pan.lock.core.key.StandardKeyGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 锁注解
 */
@Documented
@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 锁名称
     * @return
     */
    String name() default "";

    /**
     * 尝试上锁的过期时间
     * @return
     */
    long expireSeconds() default 60L;

    /**
     * 锁的key 支持el表达式
     * @return
     */
    String[] keys() default {};

    /**
     * 锁的key生成器
     * @return
     */
    Class<? extends KeyGenerator> keyGenerator() default StandardKeyGenerator.class;
}

