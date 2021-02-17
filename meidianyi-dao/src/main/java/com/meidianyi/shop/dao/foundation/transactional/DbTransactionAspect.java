package com.meidianyi.shop.dao.foundation.transactional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author lixinguo
 */
@Aspect
@Configuration
public class DbTransactionAspect {

    @Autowired
    DbMainTransactionDao dbMainTransactionDao;

    @Autowired
    DbShopTransactionDao dbShopTransactionDao;

    /**
     * 注解切面
     */
    @Pointcut("@annotation(com.meidianyi.shop.dao.foundation.transactional.DbTransactional)")
    public void transactional() {
    }

    /**
     * 注解的切面处理
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "transactional()")
    public Object transactionalProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        DbTransactional dbTransactional = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(DbTransactional.class);

        Object[] result = new Object[1];
        if (dbTransactional.type() == DbType.MAIN_DB) {
            dbMainTransactionDao.transaction(() -> {
                result[0] = joinPoint.proceed();
            });
        }

        if (dbTransactional.type() == DbType.SHOP_DB) {
            dbShopTransactionDao.transaction(() -> {
                result[0] = joinPoint.proceed();
            });
        }
        return result[0];
    }
}
