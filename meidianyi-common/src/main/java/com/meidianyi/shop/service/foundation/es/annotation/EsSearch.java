package com.meidianyi.shop.service.foundation.es.annotation;


import org.elasticsearch.index.query.MatchQueryBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *
 * @author 卢光耀
 * @date 2019-09-30 14:28
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface EsSearch {

    /**
     * @return es存储的键值
     */
    String name() default "";

    Class queryBuilderType() default MatchQueryBuilder.class ;

}

