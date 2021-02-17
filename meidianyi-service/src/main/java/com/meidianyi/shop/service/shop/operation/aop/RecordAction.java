package com.meidianyi.shop.service.shop.operation.aop;


import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author: 卢光耀
 * @date: 2019-07-17 14:37
 *
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RecordAction {
    RecordContentTemplate[] templateId();
    String[] templateData();
}
