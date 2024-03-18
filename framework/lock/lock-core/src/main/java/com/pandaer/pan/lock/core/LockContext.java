package com.pandaer.pan.lock.core;

import com.pandaer.pan.lock.core.annotation.Lock;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Data
public class LockContext {

    /**
     * 切点方法所属类的名称
     */
    private String className;

    /**
     * 切点方法名称
     */
    private String methodName;

    /**
     * 锁注解

     */
    private Lock annotation;

    /**
     * 切点所属类的类型
     */
    private Class<?> classType;

    /**
     * 切点方法
     */
    private Method method;

    /**
     * 切点方法的参数
     */
    private Object[] args;

    /**
     * 切点方法的参数类型
     */
    private Class<?>[] parameterType;

    /**
     * 具体的类对象
     */
    private Object target;

    public static LockContext init(ProceedingJoinPoint joinPoint) {
        LockContext lockContext = new LockContext();
        doInit(lockContext,joinPoint);
        return lockContext;

    }

    /**
     * 默认的初始化方法
     * @param lockContext
     * @param joinPoint
     */
    private static void doInit(LockContext lockContext, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> classType = signature.getDeclaringType();
        String className = signature.getDeclaringTypeName();
        Method method = signature.getMethod();
        String methodName = signature.getName();
        Class<?>[] parameterTypes = signature.getParameterTypes();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Lock lockAnnotation = method.getAnnotation(Lock.class);
        lockContext.setClassName(className);
        lockContext.setMethodName(methodName);
        lockContext.setAnnotation(lockAnnotation);
        lockContext.setClassType(classType);
        lockContext.setMethod(method);
        lockContext.setArgs(args);
        lockContext.setParameterType(parameterTypes);
        lockContext.setTarget(target);
    }

}
