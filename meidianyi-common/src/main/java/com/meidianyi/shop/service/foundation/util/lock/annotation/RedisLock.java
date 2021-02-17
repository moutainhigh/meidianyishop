package com.meidianyi.shop.service.foundation.util.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis批量锁
 * 以下三注解配合使用
 * @RedisLock（方法注解；必选）
 * @RedisLockKeys(方法形参注解；必选)
 * @RedisLockField（自定义类型属性；非必选）
 * param type eg:
 *               List<jdk自带类型> List<自定义类型>
 *               jdk自带类型
 *               自定义类型
 *               (jdk自带类型支持基本类型包装类、string等)
 * 实际redis key 为 @RedisLock.prefix + shopId + obj.toString()
 * @author 王帅
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisLock {

    /**
     * 轮询锁等待时间（毫秒）
     */
    long maxWait() default 3 *1000;

    /**
     * 轮询锁最大次数
     */
    int pollingLimit() default 30;

    /**
     * 锁过期时间（毫秒）
     */
    int expiredTime() default 2 * 60 * 1000;

    /**
     * key前缀
     */
    String prefix();

    /**
     * key前缀
     */
    boolean noResubmit() default false;
}
