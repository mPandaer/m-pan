package com.pandaer.pan.lock.core.aspect;


import com.pandaer.pan.core.exception.MPanFrameworkException;
import com.pandaer.pan.lock.core.LockContext;
import com.pandaer.pan.lock.core.key.KeyGenerator;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Component
@Aspect
@Log4j2
@Data
public class LockAspect implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private LockRegistry lockRegistry;

    @Autowired
    public void setLockRegistry(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Pointcut("@annotation(com.pandaer.pan.lock.core.annotation.Lock)")
    public void lockPointcut() {
    }

    @Around("lockPointcut()")
    public Object aroundLock(ProceedingJoinPoint joinPoint){
        Object result = null;
        LockContext lockContext = LockContext.init(joinPoint);
        Lock lock = checkAndGetLock(lockContext);
        if (Objects.isNull(lock)) {
            log.error("获取锁失败");
            throw new MPanFrameworkException("LockAspect 获取锁失败");
        }
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock(lockContext.getAnnotation().expireSeconds(), TimeUnit.SECONDS);
            if (lockResult) {
                result = joinPoint.proceed(joinPoint.getArgs());
            }
        } catch (Throwable e) {
            log.error("获取锁失败", e);
            throw new MPanFrameworkException("LockAspect--aroundLock 获取锁失败");
        } finally {
            if (lockResult) {
                lock.unlock();
            }
        }
        return result;
    }

    private Lock checkAndGetLock(LockContext lockContext) {
        if (Objects.isNull(lockRegistry)) {
            log.error("LockRegistry is null");
            return null;
        }
        String lockKey = getLockKey(lockContext);
        if (Objects.isNull(lockKey)) {
            log.error("lockKey is null");
            return null;
        }
        return lockRegistry.obtain(lockKey);
    }

    private String getLockKey(LockContext lockContext) {
        KeyGenerator bean = applicationContext.getBean(lockContext.getAnnotation().keyGenerator());
        if (Objects.isNull(bean)) {
            log.error("KeyGenerator is null");
            return null;
        }
        return bean.generateKey(lockContext);
    }


}
