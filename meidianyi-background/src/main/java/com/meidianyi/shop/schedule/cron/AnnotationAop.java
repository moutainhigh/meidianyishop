package com.meidianyi.shop.schedule.cron;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * The type Annotation aop.
 *
 * @author liufei
 * @date 12 /22/19
 */
@Aspect
@Component
public class AnnotationAop {
    /**
     * The Registrar.
     */
    @Autowired
    CronTaskRegistrar registrar;

    /**
     * Log before v 1.
     *
     * @param joinPoint the join point
     */
    @Before("execution(* CronRunnable.run(..))")
    public void logBeforeV1(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        byte num = registrar.findByCronKey(className).getRetriesNum();
        modifyAnnotation(CronRunnable.class, Retryable.class, "run", "maxAttempts", String.valueOf(num));
    }

    /**
     * Modify annotation annotation.
     *
     * @param className      the class name
     * @param annotationName the annotation name
     * @param methodName     the method name
     * @param modifyField    the modify field
     * @param paramName      the param name
     * @param paramTypes     the param types
     * @return the annotation
     */
    public <T, R extends Annotation> Annotation modifyAnnotation(Class<T> className, Class<R> annotationName, String methodName, String modifyField, String paramName, Class<?>... paramTypes) {
        try {
            Method method = className.getDeclaredMethod(methodName, paramTypes);
            Annotation annotation = method.getAnnotation(annotationName);
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
            memberValues.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Object> values = (Map<String, Object>) memberValues.get(invocationHandler);
            System.out.println("maxAttempts当前值为：" + values.get("maxAttempts"));
            values.put(modifyField, paramName);
            return annotation;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
