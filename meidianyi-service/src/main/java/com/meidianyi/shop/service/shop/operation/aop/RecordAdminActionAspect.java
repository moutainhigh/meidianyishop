package com.meidianyi.shop.service.shop.operation.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;

/**
 * 操作记录切面
 * @author: 卢光耀
 * @date: 2019-07-12 09:49
 *
*/
@Aspect
@Configuration
public class RecordAdminActionAspect {
	@Autowired RecordAdminActionService service;
    @Pointcut("@annotation(com.meidianyi.shop.service.shop.operation.aop.RecordAction)")
    public void recordAspect(){

    }

    @AfterReturning(value = "recordAspect()")
    public void returnRunning(JoinPoint point){
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        RecordAction recordAction = method.getAnnotation(RecordAction.class);

        System.out.println(recordAction);
        System.out.println(recordAction.templateId());
        
        RecordContentTemplate[] templateId = recordAction.templateId();
        List<Integer> result = new ArrayList<>();
        for(RecordContentTemplate t: templateId) {
        	result.add(t.getCode());
        }
        		
        service.insertRecord(result, recordAction.templateData());
        if ( attributes != null){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
        }else {

        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();

    }
}
