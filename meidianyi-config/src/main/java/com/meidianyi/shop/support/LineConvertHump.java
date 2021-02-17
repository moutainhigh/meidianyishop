package com.meidianyi.shop.support;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author lixinguo
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface LineConvertHump {

}
