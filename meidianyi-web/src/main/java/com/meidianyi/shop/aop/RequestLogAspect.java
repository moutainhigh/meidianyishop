package com.meidianyi.shop.aop;


import cn.hutool.json.JSONUtil;
import com.google.common.base.Stopwatch;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.RequestUtil;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.exception.MpException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * controller所有请求日志记录
 * @author 卢光耀
 * @date 2019-08-06 14:14
 *
*/
@Slf4j
@Aspect
@Configuration
@ConditionalOnProperty(prefix="local",name = "log", havingValue = "on")
public class RequestLogAspect {
    @Pointcut("execution(public com.meidianyi.shop.common.foundation.data.JsonResult com.meidianyi.shop.controller..*Controller.*(..))")
    public void controllerLogAspect(){}

    @Around(value = "controllerLogAspect()")
    public JsonResult beforeRequest(ProceedingJoinPoint point) throws Throwable {
        StringBuilder logAfterStr = new StringBuilder();
        JsonResult result;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();
        String token = request.getHeader("V-Token");
        String ip = RequestUtil.getIp(request);
        String methodName = point.getSignature().getName();
        String timestamp = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL);
        logAfterStr.append("\n");
        logAfterStr.append("#####################Request#####################").append("\n");
        logAfterStr.append("Timestamp    :").append(timestamp).append("\n");
        logAfterStr.append("MethodName   :").append(methodName).append("\n");
        logAfterStr.append("UserToken    :").append(token).append("\n");
        logAfterStr.append("RequestIP    :").append(ip).append("\n");
        logAfterStr.append("RequestURI   :").append(request.getRequestURI()).append("\n");
        logAfterStr.append("RequestParams:");
        if( point.getArgs() != null && point.getArgs().length > 0){
            logAfterStr.append(JSONUtil.toJsonStr(point.getArgs()));
        }
        log.info(logAfterStr.toString());
        Stopwatch stopwatch = Stopwatch.createStarted();
        try{
            StringBuilder logResponseStr = new StringBuilder();
            result = (JsonResult) point.proceed();

            logResponseStr.append("\n");
            logResponseStr.append("#####################Response#####################").append("\n");
            logResponseStr.append("Timestamp    :").append(timestamp).append("\n");
            logResponseStr.append("MethodName   :").append(methodName).append("\n");
            logResponseStr.append("UserToken    :").append(token).append("\n");
            logResponseStr.append("RequestIP    :").append(ip).append("\n");
            logResponseStr.append("RequestURI   :").append(request.getRequestURI()).append("\n");
            logResponseStr.append("RunTime      :").append(stopwatch.elapsed(TimeUnit.MILLISECONDS)).append("ms\n");
            logResponseStr.append("JsonResult   :").append(result);
            log.info(logResponseStr.toString());
        }catch (Throwable e){
            // 如果是自定义的业务异常, 继续抛出让ExceptionControllerHandler去捕获, 这样可以回显前端详细的错误信息, 而不是简单的"操作失败"
            if (e instanceof BusinessException || e instanceof MpException) {
                throw e;
            }
            StringBuilder logErrorStr = new StringBuilder();
            logErrorStr.append("\n");
            logErrorStr.append("#####################Exception#####################").append("\n");
            logErrorStr.append("Timestamp    :").append(timestamp).append("\n");
            logErrorStr.append("MethodName   :").append(methodName).append("\n");
            logErrorStr.append("UserToken    :").append(token).append("\n");
            logErrorStr.append("RequestIP    :").append(ip).append("\n");
            logErrorStr.append("RequestURI   :").append(request.getRequestURI()).append("\n");
            logErrorStr.append("Exception    :").append(e.getMessage()).append("\n");
            e.printStackTrace();
            log.error(logErrorStr.toString());
            log.error("ExceptionStackTrace   :",e);
            return new JsonResult().fail("zh_CN", JsonResultCode.CODE_FAIL);
        }
        return result;
    }

}
