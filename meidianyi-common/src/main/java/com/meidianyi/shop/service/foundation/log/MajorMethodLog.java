package com.meidianyi.shop.service.foundation.log;

import com.google.common.base.Stopwatch;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * The type Major method log.
 *
 * @author liufei
 * @date 12 /31/19
 */
@Slf4j
@Aspect
@Configuration
@ConditionalOnProperty(prefix = "local", name = "log", havingValue = "on")
public class MajorMethodLog {

    /**
     * Major log aspect.
     */
    @Pointcut("@annotation(com.meidianyi.shop.service.foundation.log.LogAspect)")
    public void majorLogAspect() {
    }


    /**
     * Around method.
     *
     * @param point the point
     * @throws Throwable the throwable
     */
    @Around(value = "majorLogAspect()")
    public void aroundMethod(ProceedingJoinPoint point) throws Throwable {
        StringBuilder logAfterStr = new StringBuilder();
        String methodName = point.getSignature().getName();
        String timestamp = DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL);
        logAfterStr.append("\n");
        logAfterStr.append("——————————————————————CHAIN—NODE—BEGIN———————————————————").append("\n");
        logAfterStr.append("Timestamp    :").append(timestamp).append("\n");
        logAfterStr.append("MethodName   :").append(methodName).append("\n");
        logAfterStr.append("RequestParams:").append("\n");
        if (point.getArgs() != null && point.getArgs().length > 0) {
            Arrays.stream(point.getArgs()).
                forEach(arg -> {
                    String paramName = Objects.nonNull(arg) ? arg.getClass().getSimpleName() : "NULL";
                    logAfterStr.append("——————param：").append(paramName).append("——").append(Util.toPrettyJson(arg)).append("\n");
                });
        }
        log.info(logAfterStr.toString());
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            StringBuilder logResponseStr = new StringBuilder();
            Object response = point.proceed();
            logResponseStr.append("\n");
            logResponseStr.append("——————————————————————CHAIN—NODE—END——————————————————").append("\n");
            logResponseStr.append("Timestamp    :").append(timestamp).append("\n");
            logResponseStr.append("MethodName   :").append(methodName).append("\n");
            logResponseStr.append("RunTime      :").append(stopwatch.elapsed(TimeUnit.MILLISECONDS)).append("ms\n");
            String responseName = Objects.nonNull(response) ? response.getClass().getSimpleName() : "NULL";
            logResponseStr.append("ResponseParam   :").append(responseName).append("——").append(Util.toPrettyJson(response)).append("\n");
            log.info(logResponseStr.toString());
        } catch (Throwable e) {
            StringBuilder logErrorStr = new StringBuilder();
            logErrorStr.append("\n");
            logErrorStr.append("——————————————————————CHAIN—NODE—EXCEPTION——————————————————").append("\n");
            logErrorStr.append("Timestamp    :").append(timestamp).append("\n");
            logErrorStr.append("MethodName   :").append(methodName).append("\n");
            logErrorStr.append("Exception    :").append(ExceptionUtils.getStackTrace(e)).append("\n");
            log.error(logErrorStr.toString());
            throw e;
        }
    }
}
