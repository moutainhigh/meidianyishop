package com.meidianyi.shop.service.foundation.es.annotation;

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

public @interface EsFiled {

    /**
     * @return es存储的键值
     */
    String name();

    /**
     * @return es中对应的类型
     */
    String type() ;

    /**
     * @return 启用分词器并指定使用的分词器
     */
    String analyzer() default "";
    /**
     * @return 启用分词器并指定使用的分词器
     */
    String searchAnalyzer() default "";

    /**
     * @return 是否建立索引(字段能不能被查询/搜索)
     */
    boolean index() default true;

    /**
     * @return 缩放因子
     */
    String scaledNumber() default "100";

    /**
     * @return 复制字段
     */
    String copyTo() default "" ;

    /**
     * 所有的数字、地理坐标、日期、IP 和不分析（ not_analyzed ）字符类型都会默认开启.
     * 这里对其进行优化，先关闭一些没有用到聚合、排序、脚本等操作的字段。
     * @return 是否启用列式存储(列式存储 适用于聚合、排序、脚本等操作)
     */
    boolean doc_values() default true;


}
