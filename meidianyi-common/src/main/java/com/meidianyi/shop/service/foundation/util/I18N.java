package com.meidianyi.shop.service.foundation.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 国际化字段
 *
 * @author 郑保乐
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18N {

    /**
     * static.i18n 目录下的 properties 文件名称
     */
    String propertiesFileName();
}
