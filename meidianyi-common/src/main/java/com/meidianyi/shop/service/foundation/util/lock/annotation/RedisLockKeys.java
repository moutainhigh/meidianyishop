package com.meidianyi.shop.service.foundation.util.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis批量锁keys
 * keys type : list<Object> or Object
 * Object type : jdk自带类型 如基本类型包装类、string等
 *               自定义类型 需要配合@RedisLockField注解该自定义类型的属性
 * @author 王帅
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RedisLockKeys {
}
