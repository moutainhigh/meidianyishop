package com.meidianyi.shop.common.foundation.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 李晓冰
 * @date 2019年07月18日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumn {

    /**
     * 列表顺序0开始
     * @return
     */
    int columnIndex() default -1;

    /**
     * 对应的excel列头
     * @return
     */
    String columnName() default "";

    /**
     * 模板信息填充值
     * @return
     */
    String[] args() default {};
}
